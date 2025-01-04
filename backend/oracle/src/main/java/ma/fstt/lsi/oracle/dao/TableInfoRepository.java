package ma.fstt.lsi.oracle.dao;

import ma.fstt.lsi.oracle.model.TableInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableInfoRepository extends JpaRepository<TableInfo, Long> {
    TableInfo findByTableName(String tableName);
}