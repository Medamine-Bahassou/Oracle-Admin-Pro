package ma.fstt.lsi.oracle.controller;

import ma.fstt.lsi.oracle.model.BackupLog;
import ma.fstt.lsi.oracle.dao.BackupLogRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/backup/logs")
public class BackupLogController {

    private final BackupLogRepository backupLogRepository;

    public BackupLogController(BackupLogRepository backupLogRepository) {
        this.backupLogRepository = backupLogRepository;
    }

    @GetMapping
    public List<BackupLog> getAllLogs() {
        return backupLogRepository.findAll();
    }
}
