package ma.fstt.lsi.oracle.dao;

import ma.fstt.lsi.oracle.model.BackupHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {
    List<BackupHistory> findByBackupDateBetween(LocalDateTime start, LocalDateTime end);
}