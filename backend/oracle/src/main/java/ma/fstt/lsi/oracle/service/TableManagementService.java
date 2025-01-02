package ma.fstt.lsi.oracle.service;

import jakarta.transaction.Transactional;
import ma.fstt.lsi.oracle.dao.TableInfoRepository;
import ma.fstt.lsi.oracle.model.TableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableManagementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TableInfoRepository tableInfoRepository;

    @Transactional
    public void createTable(String tableName, List<String> columns) {
        // Define primary key column
        String primaryKeyColumn = "id NUMBER PRIMARY KEY";

        // Build column definitions
        String columnDefinitions = columns.stream()
                .map(col -> col + " VARCHAR2(255)")
                .collect(Collectors.joining(", "));

        // Create table
        String createTableSql = "CREATE TABLE " + tableName + " (" + primaryKeyColumn + ", " + columnDefinitions + ")";
        jdbcTemplate.execute(createTableSql);

        // Create sequence
        String sequenceName = tableName + "_seq";
        String createSequenceSql = "CREATE SEQUENCE " + sequenceName + " START WITH 1 INCREMENT BY 1";
        jdbcTemplate.execute(createSequenceSql);

        // Create trigger for auto-increment
        String triggerName = tableName + "_trg";
        String createTriggerSql = "CREATE OR REPLACE TRIGGER " + triggerName +
                " BEFORE INSERT ON " + tableName +
                " FOR EACH ROW " +
                " BEGIN " +
                "   :NEW.id := " + sequenceName + ".NEXTVAL; " +
                " END;";
        jdbcTemplate.execute(createTriggerSql);

        // Save table metadata
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        tableInfo.setColumns(columns);
        tableInfoRepository.save(tableInfo);
    }

    public List<TableInfo> listTables() {
        return tableInfoRepository.findAll();
    }

    public TableInfo getTableInfo(String tableName) {
        return tableInfoRepository.findByTableName(tableName);
    }

    @Transactional
    public void editTable(String tableName, List<String> newColumns) {
        TableInfo tableInfo = tableInfoRepository.findByTableName(tableName);
        if (tableInfo != null) {
            List<String> existingColumns = tableInfo.getColumns();

            // Add new columns
            for (String newColumn : newColumns) {
                if (!existingColumns.contains(newColumn)) {
                    String addColumnSql = "ALTER TABLE " + tableName + " ADD " + newColumn + " VARCHAR2(255)";
                    jdbcTemplate.execute(addColumnSql);
                }
            }

            // Remove old columns
            for (String oldColumn : existingColumns) {
                if (!newColumns.contains(oldColumn)) {
                    String dropColumnSql = "ALTER TABLE " + tableName + " DROP COLUMN " + oldColumn;
                    jdbcTemplate.execute(dropColumnSql);
                }
            }

            tableInfo.setColumns(newColumns);
            tableInfoRepository.save(tableInfo);
        }
    }

    @Transactional
    public void deleteTable(String tableName) {
        // Drop trigger
        String triggerName = tableName + "_trg";
        String dropTriggerSql = "DROP TRIGGER " + triggerName;
        jdbcTemplate.execute(dropTriggerSql);

        // Drop sequence
        String sequenceName = tableName + "_seq";
        String dropSequenceSql = "DROP SEQUENCE " + sequenceName;
        jdbcTemplate.execute(dropSequenceSql);

        // Drop table
        String dropTableSql = "DROP TABLE " + tableName;
        jdbcTemplate.execute(dropTableSql);

        // Remove metadata
        TableInfo tableInfo = tableInfoRepository.findByTableName(tableName);
        if (tableInfo != null) {
            tableInfoRepository.delete(tableInfo);
        }
    }

    public List<String> listTableNames() {
        return tableInfoRepository.findAll().stream()
                .map(TableInfo::getTableName)
                .collect(Collectors.toList());
    }
    public List<TableInfo> listTable() {
        return new ArrayList<>(tableInfoRepository.findAll());
    }

    public List<String> listColumns(String tableName) {
        TableInfo tableInfo = tableInfoRepository.findByTableName(tableName);
        return tableInfo.getColumns();
    }
}
