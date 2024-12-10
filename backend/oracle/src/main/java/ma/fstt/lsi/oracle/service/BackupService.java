package ma.fstt.lsi.oracle.service;

import ma.fstt.lsi.oracle.model.BackupLog;
import ma.fstt.lsi.oracle.dao.BackupLogRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;

@Service
public class BackupService {

    private final DataSource dataSource;
    private final BackupLogRepository backupLogRepository;

    public BackupService(DataSource dataSource, BackupLogRepository backupLogRepository) {
        this.dataSource = dataSource;
        this.backupLogRepository = backupLogRepository;
    }

    public void executeBackup(String backupCommand) {
        executeCommand(backupCommand, "Backup");
    }

    public void executeRestore(String restoreCommand) {
        executeCommand(restoreCommand, "Restore");
    }
    @Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2 AM
    public void scheduleBackup() {
        String automaticBackupCommand = "BACKUP DATABASE AS COPY TO DISK '/backup/path'";
        executeCommand(automaticBackupCommand, "Scheduled Backup");
    }
    private void executeCommand(String command, String operation) {
        BackupLog log = new BackupLog();
        log.setOperation(operation);
        log.setExecutedAt(LocalDateTime.now());

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(command);
            log.setStatus("Success");
            log.setDetails("Command executed successfully.");
        } catch (Exception e) {
            log.setStatus("Failure");
            log.setDetails(e.getMessage());
        }

        backupLogRepository.save(log);
    }
}
