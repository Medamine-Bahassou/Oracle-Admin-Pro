package ma.fstt.lsi.oracle.dao;

import ma.fstt.lsi.oracle.model.BackupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BackupScheduleRepository extends JpaRepository<BackupSchedule, Long> {
    Optional<BackupSchedule> findByActive(boolean active);
}