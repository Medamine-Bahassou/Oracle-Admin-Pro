package ma.fstt.lsi.oracle.dao;

import ma.fstt.lsi.oracle.model.StatisticsJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsJobRepository extends JpaRepository<StatisticsJob, Long> {
    List<StatisticsJob> findByObjectType(String objectType);
}