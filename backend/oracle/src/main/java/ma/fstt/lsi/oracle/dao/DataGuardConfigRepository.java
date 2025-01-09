package ma.fstt.lsi.oracle.dao;

import ma.fstt.lsi.oracle.model.DataGuardConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataGuardConfigRepository extends JpaRepository<DataGuardConfig, Long> {
}
