package ma.fstt.lsi.oracle.controller;

import ma.fstt.lsi.oracle.service.BackupService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @PostMapping("/execute")
    public String executeBackup(@RequestBody String command) {
        try {
            backupService.executeBackup(command);
            return "Backup executed successfully.";
        } catch (Exception e) {
            return "Backup failed: " + e.getMessage();
        }
    }

    @PostMapping("/restore")
    public String executeRestore(@RequestBody String command) {
        try {
            backupService.executeRestore(command);
            return "Restore executed successfully.";
        } catch (Exception e) {
            return "Restore failed: " + e.getMessage();
        }
    }
}
