package ma.fstt.lsi.oracle.controller;

import lombok.RequiredArgsConstructor;
import ma.fstt.lsi.oracle.model.BackupHistory;

import ma.fstt.lsi.oracle.service.RmanServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

    private final RmanServiceImpl rmanService;

    @PostMapping("/full")
    public ResponseEntity<String> performFullBackup() {
        String result = rmanService.performFullBackup();
        return ResponseEntity.ok(result);
        //hi
    }

    @PostMapping("/incremental")
    public ResponseEntity<String> performIncrementalBackup(@RequestParam int level) {
        String result = rmanService.performIncrementalBackup(level); 
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<
            BackupHistory>> getBackupHistory() {
        List<BackupHistory> history = rmanService.listBackups();
        return ResponseEntity.ok(history);
    }

    @PostMapping("/restore")
    public ResponseEntity<String> performRestore() {
        String result = rmanService.performRestore();
        return ResponseEntity.ok(result);
    }
    @GetMapping("/history/date")
    public ResponseEntity<List<BackupHistory>> getBackupHistoryByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<BackupHistory> history = rmanService.getBackupHistoryByDateRange(startDate, endDate);
        return ResponseEntity.ok(history);
    }
}
