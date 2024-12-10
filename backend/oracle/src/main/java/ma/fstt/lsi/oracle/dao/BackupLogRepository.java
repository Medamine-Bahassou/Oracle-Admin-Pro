package ma.fstt.lsi.oracle.dao;

import ma.fstt.lsi.oracle.model.BackupLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupLogRepository extends JpaRepository<BackupLog, Long> {
}