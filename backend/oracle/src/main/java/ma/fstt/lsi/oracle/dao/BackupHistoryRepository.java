package ma.fstt.lsi.oracle.dao;

import ma.fstt.lsi.oracle.model.BackupHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {
    List<BackupHistory> findByBackupDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM BackupHistory b WHERE b.backupDate BETWEEN :startDate AND :endDate")
    List<BackupHistory> findBackupHistoryByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}