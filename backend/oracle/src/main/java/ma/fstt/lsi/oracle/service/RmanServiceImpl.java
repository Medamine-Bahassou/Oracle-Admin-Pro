package ma.fstt.lsi.oracle.service;

import lombok.extern.slf4j.Slf4j;
import ma.fstt.lsi.oracle.dao.BackupHistoryRepository;
import ma.fstt.lsi.oracle.model.BackupHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class RmanServiceImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BackupHistoryRepository backupHistoryRepository;


    @Value("${docker.container.name:}") // Empty default value means not in docker
    private String dockerContainerName;

    @Value("classpath:rman/backup_script.rman")
    private Resource fullBackupScript;

    @Value("classpath:rman/incremental_level_0_backup.rman")
    private Resource incrementalLevel0Script;

    @Value("classpath:rman/incremental_level_1_backup.rman")
    private Resource incrementalLevel1Script;

    @Value("classpath:rman/restore_script.rman")
    private Resource restoreScript;

    @Value("classpath:sql/open_db.sql")
    private Resource openDbScript;
    @Value("${spring.datasource.url}")
    private String dataSourceUrl;
    private String getConnectionDetails(){

        if(dataSourceUrl.contains("@")){
            return dataSourceUrl.substring(dataSourceUrl.indexOf("@")+1);
        }else{
            return "";
        }
    }
    // Method to determine the appropriate execution command
    private String buildExecutionCommand(String rmanCommand) {
        if (dockerContainerName != null && !dockerContainerName.isEmpty()) {
            return String.format("docker exec %s %s", dockerContainerName, rmanCommand);
        }
        return rmanCommand;
    }
    private String executeRmanProcess(String command,String operationType) {
        String result = operationType + " Failed";
        String resultBackupHistory = operationType + " Failed";
        String status = "FAILURE";
        StringBuilder output = new StringBuilder();
        try {
            // Start the process
            Process process = Runtime.getRuntime().exec(command);
            // Capture standard and error outputs using try-with-resources
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

                String line;
                // Read standard output
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                // Read error output
                while ((line = errorReader.readLine()) != null) {
                    output.append("ERROR: ").append(line).append("\n");
                }
            }
            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Update result based on exit code
            if (exitCode == 0) {
                result = operationType+ " Successful\n" + output;
                resultBackupHistory = operationType+ " Successful";
                status = "SUCCESS";
                log.info(operationType + " completed successfully.\n{}",output);
            } else {
                result = operationType+ " Failed with exit code: " + exitCode + "\n" + output;
                log.error(operationType +" failed. Exit code: {}, Output:\n{}",exitCode, output);
            }
        } catch (IOException | InterruptedException e) {
            // Handle exceptions during execution
            result = "Error during " +operationType +" execution: " + e.getMessage();
            log.error("Error during " + operationType + " execution",e);
        }
        // Save backup record
        backupHistoryRepository.save(new BackupHistory(operationType, status, LocalDateTime.now(), resultBackupHistory));

        return result;
    }
    // Helper method to read the content of a resource
    private String readResourceContent(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        }
    }

    public String performFullBackup() {
        try {
            String rmanScript = readResourceContent(fullBackupScript);
            // log.info("Rman Script content is: {}", rmanScript);
            String rmanCommand = String.format("rman target /@%s cmdfile=%s",getConnectionDetails(),createTempFile(rmanScript, "backup_script.rman"));

            String command = buildExecutionCommand(rmanCommand);
            return executeRmanProcess(command,"Full Backup");

        } catch (IOException e) {
            log.error("Error reading full backup script", e);
            return "Error during full backup execution: " + e.getMessage();
        }
    }


    public String performIncrementalBackup(int level) {

        try {
            Resource scriptResource;
            if (level == 0) {
                scriptResource = incrementalLevel0Script;
            } else {
                scriptResource = incrementalLevel1Script;
            }

            String rmanScript = readResourceContent(scriptResource);

            String rmanCommand = String.format("rman target /@%s cmdfile=%s",getConnectionDetails(),createTempFile(rmanScript,  "incremental_backup.rman"));
            String command = buildExecutionCommand(rmanCommand);
            return executeRmanProcess(command,"Incremental Backup");
        } catch (IOException e) {
            log.error("Error reading incremental backup script", e);
            return "Error during incremental backup execution: " + e.getMessage();
        }

    }

    public List<BackupHistory> listBackups() {
        return backupHistoryRepository.findAll();
    }


    public String performRestore() {
        String result = "Restore Failed";
        String resultBackupHistory = "Restore Failed";
        String status = "FAILURE";
        StringBuilder output = new StringBuilder();
        try {
            String rmanScript = readResourceContent(restoreScript);
            String rmanCommand = String.format("rman target /@%s cmdfile=%s",getConnectionDetails(),createTempFile(rmanScript, "restore_script.rman"));
            // Step 1: Execute RMAN restore script

            String command = buildExecutionCommand(rmanCommand);
            Process rmanProcess = Runtime.getRuntime().exec(command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(rmanProcess.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(rmanProcess.getErrorStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                while ((line = errorReader.readLine()) != null) {
                    output.append("ERROR: ").append(line).append("\n");
                }
            }

            int rmanExitCode = rmanProcess.waitFor();
            if (rmanExitCode != 0) {
                result =  "Restore failed with exit code: " + rmanExitCode + "\n" + output;
                log.error("Restore failed with exit code: {}. \n Output: {}", rmanExitCode,output);
                return result;
            }

            String sqlScript = readResourceContent(openDbScript);
            String sqlCommand = String.format("sqlplus /@%s as sysdba cmdfile=%s",getConnectionDetails(),createTempFile(sqlScript, "open_db.sql"));
            String  sqlProcessCommand = buildExecutionCommand(sqlCommand);
            Process sqlProcess = Runtime.getRuntime().exec(sqlProcessCommand);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(sqlProcess.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(sqlProcess.getErrorStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                while ((line = errorReader.readLine()) != null) {
                    output.append("ERROR: ").append(line).append("\n");
                }
            }

            int sqlExitCode = sqlProcess.waitFor();

            if (sqlExitCode == 0) {
                result = "Restore and database open successful\n" + output;
                resultBackupHistory = "Restore and open done successfully";
                status = "SUCCESS";
                log.info("Restore and database open successful\nOutput:\n{}",output);
            } else {
                result = "Failed to open the database with exit code: " + sqlExitCode + "\n" + output;
                log.error("Failed to open the database with exit code: {}.\n Output: {}", sqlExitCode,output);
            }


        } catch (IOException | InterruptedException e) {
            result = "Error during restore and open execution: " + e.getMessage();
            log.error("Error during restore and open execution",e);
        }

        // Log the restore operation in backup history
        backupHistoryRepository.save(new BackupHistory("RESTORE", status, LocalDateTime.now(), resultBackupHistory));

        return result;
    }
    private  String createTempFile(String content, String fileName) throws IOException {
        File tempFile = File.createTempFile(fileName, ".tmp");
        try (FileWriter fw = new FileWriter(tempFile);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
        }
        tempFile.deleteOnExit();
        return tempFile.getAbsolutePath();
    }
    public List<BackupHistory> getBackupHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return backupHistoryRepository.findBackupHistoryByDateRange(startDate, endDate);
    }
}