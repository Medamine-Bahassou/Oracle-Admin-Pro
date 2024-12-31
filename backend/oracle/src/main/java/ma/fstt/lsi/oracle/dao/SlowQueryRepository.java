package ma.fstt.lsi.oracle.dao;

import ma.fstt.lsi.oracle.model.SlowQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlowQueryRepository extends JpaRepository<SlowQuery, Long> {
    List<SlowQuery> findByStatusOrderByElapsedTimeDesc(String status);
}
