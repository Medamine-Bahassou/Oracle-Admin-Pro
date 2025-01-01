package ma.fstt.lsi.oracle.service;

import lombok.extern.slf4j.Slf4j;
import ma.fstt.lsi.oracle.dao.BackupHistoryRepository;
import ma.fstt.lsi.oracle.model.BackupHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class RmanServiceImpl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BackupHistoryRepository backupHistoryRepository;


    public String performFullBackup() {
        String result = "Backup Failed"; // Default result message
        String result_backup_history = "Backup Failed";
        String status = "FAILURE";

        try {
            // Command to execute the RMAN script inside the Docker container
            String command = "docker exec oracle19c rman target / cmdfile=/tmp/backup_script.rman";

            // Start the process
            Process process = Runtime.getRuntime().exec(command);

            // Capture standard and error outputs using try-with-resources
            StringBuilder output = new StringBuilder();
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
                result = "Backup Successful\n" + output.toString();
                result_backup_history = "Backup Successful";
                status = "SUCCESS";
            } else {
                result = "Backup Failed with exit code: " + exitCode + "\n" + output.toString();
            }
        } catch (IOException | InterruptedException e) {
            // Handle exceptions during execution
            result = "Error during backup execution: " + e.getMessage();
        }

        // Save backup record
        backupHistoryRepository.save(new BackupHistory("FULL", status, LocalDateTime.now(), result_backup_history));
        return result;
    }

    public List<BackupHistory> getBackupHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return backupHistoryRepository.findBackupHistoryByDateRange(startDate, endDate);
    }

    public String performIncrementalBackup(int level) {
        String result = "Backup Failed"; // Default result message

        String result_backup_history = "Backup Failed";
        String status = "FAILURE";
        try {
            // Determine the RMAN script to execute based on the level
            String scriptFile = level == 0 ? "/tmp/incremental_level_0_backup.rman"
                    : "/tmp/incremental_level_1_backup.rman";

            // Command to execute the RMAN script inside the Docker container
            String command = "docker exec oracle19c rman target / cmdfile=" + scriptFile;

            // Start the process
            Process process = Runtime.getRuntime().exec(command);

            // Capture standard and error outputs using try-with-resources
            StringBuilder output = new StringBuilder();
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
                result = "Incremental Backup Successful\n" + output.toString();
                result_backup_history = "Incremental Backup Successful";
                status = "SUCCESS";
            } else {
                result = "Incremental Backup Failed with exit code: " + exitCode + "\n" + output.toString();
            }
        } catch (IOException | InterruptedException e) {
            // Handle exceptions during execution
            result = "Error during incremental backup execution: " + e.getMessage();
        }

        // Save backup record
        backupHistoryRepository.save(new BackupHistory("INCREMENTAL", status, LocalDateTime.now(), result_backup_history));
        return result;
    }

    public List<BackupHistory> listBackups() {
        return backupHistoryRepository.findAll();
    }


    public String performRestore() {
        String result = "Restore Failed";
        String result_backup_history = "Restore Failed";
        String status = "FAILURE";

        try {
            // Step 1: Execute RMAN restore script
            String rmanCommand = "docker exec oracle19c rman target / cmdfile=/tmp/restore_script.rman";
            Process rmanProcess = Runtime.getRuntime().exec(rmanCommand);

            StringBuilder output = new StringBuilder();
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
                return "Restore failed with exit code: " + rmanExitCode + "\n" + output.toString();
            }

            // Step 2: Execute the SQL script to open the database
//
            String sqlCommand = "docker exec -i oracle19c sqlplus / as sysdba cmdfile=/tmp/open_db.sql";

            Process sqlProcess = Runtime.getRuntime().exec(sqlCommand);

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
                result = "Restore and database open successful\n" + output.toString();
                result_backup_history = "Restore and open done successfully";
                status = "SUCCESS";
            } else {
                result = "Failed to open the database with exit code: " + sqlExitCode + "\n" + output.toString();
            }

        } catch (IOException | InterruptedException e) {
            result = "Error during restore and open execution: " + e.getMessage();
        }

        // Log the restore operation in backup history
        backupHistoryRepository.save(new BackupHistory("RESTORE", status, LocalDateTime.now(), result_backup_history));

        return result;
    }
}