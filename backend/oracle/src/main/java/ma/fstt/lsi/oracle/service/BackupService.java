package ma.fstt.lsi.oracle.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.fstt.lsi.oracle.dao.BackupHistoryRepository;
import ma.fstt.lsi.oracle.model.BackupHistory;
import ma.fstt.lsi.oracle.model.RestoreStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Service
@RequiredArgsConstructor
public class BackupService {

    @Value("${oracle.rman.command.complete}")
    private String completeBackupCommand;

    @Value("${oracle.rman.command.incremental}")
    private String incrementalBackupCommand;

    private final BackupHistoryRepository backupRepository;

    public BackupHistory triggerBackup(String type) {
        BackupHistory backup = new BackupHistory();
        backup.setType(type);
        backup.setBackupDate(LocalDateTime.now());

        try {
            // Update the command with the correct path to the script inside the Docker container
            //String command = "docker exec oracle-db rman target / cmdfile=/tmp/backup_script.rman";
            String command = "rman target / cmdfile=C:/Users/hp/Desktop/Git/Oracle Project °/Oracle-Project-/backend/oracle/src/main/java/ma/fstt/lsi/oracle/service/backup_script.rman";

            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                backup.setStatus("SUCCESS");
                backup.setBackupLocation("/backups/" + type.toLowerCase());
            } else {
                backup.setStatus("FAILURE");
                backup.setBackupLocation("N/A");
            }
        } catch (Exception e) {
            backup.setStatus("FAILURE");
            backup.setBackupLocation("N/A");
        }

        return backupRepository.save(backup);
    }
    public List<BackupHistory> getBackupHistory() {
        return backupRepository.findAll();
    }

    public List<BackupHistory> getBackupsByDateRange(LocalDateTime start, LocalDateTime end) {
        return backupRepository.findByBackupDateBetween(start, end);
    }

    @Value("${restore.timeout:300}") // 5 minutes default timeout
    private int restoreTimeout;

    @Transactional
    public RestoreStatus restoreBackup(Long id) {
        try {
            // Log the start of the restore process
            BackupHistory backup = backupRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Backup not found"));

            // Ensure the backup object is in a managed state
            backup = backupRepository.save(backup.toBuilder()
                    .restoreDate(LocalDateTime.now())
                    .status("RESTORING")
                    .build());

            RestoreStatus status = new RestoreStatus(id);

            // Restore logic
            //String command = "docker exec oracle-db rman target / cmdfile=/tmp/restore_script.rman";
            String command = "rman target / cmdfile=C:/Users/hp/Desktop/Git/Oracle Project °/Oracle-Project-/backend/oracle/src/main/java/ma/fstt/lsi/oracle/service/restore_script.rman";

            Process process = Runtime.getRuntime().exec(command);

            // Monitoring and process handling logic...

            // Update backup status after restore
            backup.setStatus(status.isCompleted() ? "RESTORED" : "RESTORE_FAILED");
            backupRepository.save(backup);

            return status;
        } catch (Exception e) {
            // Log the full exception


            // Create a detailed error status
            RestoreStatus errorStatus = new RestoreStatus(id);
            errorStatus.setError("Restore failed: " + e.getMessage());
            errorStatus.setCompleted(false);

            return errorStatus;
        }
    }



    @Scheduled(cron = "${backup.schedule.cron}")
    public void scheduledBackup() {
        triggerBackup("COMPLETE");
    }
}