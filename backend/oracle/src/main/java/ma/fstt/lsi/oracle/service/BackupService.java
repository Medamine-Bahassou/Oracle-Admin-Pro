package ma.fstt.lsi.oracle.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
            String command = "docker exec oracle-db rman target / cmdfile=/tmp/backup_script.rman";

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
        BackupHistory backup = backupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Backup not found"));

        backup.setRestoreDate(LocalDateTime.now());
        backupRepository.save(backup);

        try {
            // Start restore process
            String command = "docker exec oracle-db rman target / cmdfile=/tmp/restore_script.rman";
            Process process = Runtime.getRuntime().exec(command);

            // Create a status object to track progress
            RestoreStatus status = new RestoreStatus(id);

            // Start monitoring thread
            Thread monitorThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        status.addLog(line);
                        if (line.contains("RMAN-")) {
                            status.addProgress(5); // Increment progress on RMAN output
                        }
                    }
                } catch (IOException e) {
                    status.setError("Error reading restore output: " + e.getMessage());
                }
            });
            monitorThread.start();

            // Wait for completion or timeout
            if (!process.waitFor(restoreTimeout, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                status.setError("Restore operation timed out after " + restoreTimeout + " seconds");
                return status;
            }

            int exitCode = process.exitValue();
            if (exitCode == 0) {
                status.setCompleted(true);
                status.setProgress(100);
                status.addLog("Restore completed successfully");
            } else {
                status.setError("Restore failed with exit code: " + exitCode);
            }

            return status;

        } catch (Exception e) {
            RestoreStatus errorStatus = new RestoreStatus(id);
            errorStatus.setError("Restore failed: " + e.getMessage());
            return errorStatus;
        }
    }



    @Scheduled(cron = "${backup.schedule.cron}")
    public void scheduledBackup() {
        triggerBackup("COMPLETE");
    }
}