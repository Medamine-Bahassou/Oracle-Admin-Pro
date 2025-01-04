package ma.fstt.lsi.oracle.controller;

import lombok.RequiredArgsConstructor;
import ma.fstt.lsi.oracle.model.BackupHistory;
import ma.fstt.lsi.oracle.model.RestoreStatus;
import ma.fstt.lsi.oracle.service.BackupService;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/backups")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @PostMapping("/trigger")
    public ResponseEntity<BackupHistory> triggerBackup(@RequestParam String type) {
        return ResponseEntity.ok(backupService.triggerBackup(type));
    }

    @GetMapping("/history")
    public ResponseEntity<List<
            BackupHistory>> getBackupHistory() {
        List<BackupHistory> history = rmanService.listBackups();
        return ResponseEntity.ok(history);

    public ResponseEntity<List<BackupHistory>> getBackupHistory() {
        return ResponseEntity.ok(backupService.getBackupHistory());
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<BackupHistory>> getBackupsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(backupService.getBackupsByDateRange(start, end));
    }

    private final Map<Long, RestoreStatus> restoreStatuses = new ConcurrentHashMap<>();

    @PostMapping("/restore/{id}")
    public ResponseEntity<RestoreStatus> restoreBackup(@PathVariable Long id) {
        try {
            RestoreStatus status = backupService.restoreBackup(id);
            restoreStatuses.put(id, status);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RestoreStatus(id) {{
                        setError("Unexpected error: " + e.getMessage());
                    }});
        }
    }

    @GetMapping("/restore/{id}/status")
    public ResponseEntity<RestoreStatus> getRestoreStatus(@PathVariable Long id) {
        RestoreStatus status = restoreStatuses.get(id);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}