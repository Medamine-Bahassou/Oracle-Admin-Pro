package ma.fstt.lsi.oracle.service;

import jakarta.transaction.Transactional;
import ma.fstt.lsi.oracle.dao.EncryptionPolicyRepository;
import ma.fstt.lsi.oracle.model.EncryptionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DatabaseSecurityService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EncryptionPolicyRepository encryptionPolicyRepository;

    public void enableTDE(String tableName, String columnName) {
        String sql = "ALTER TABLE " + tableName +
                " MODIFY (" + columnName + " ENCRYPT USING 'AES256' NO SALT)";
        jdbcTemplate.execute(sql);

        EncryptionPolicy policy = new EncryptionPolicy();
        policy.setTableName(tableName);
        policy.setColumnName(columnName);
        policy.setEncryptionType("AES256");
        policy.setEnabled(true);
        encryptionPolicyRepository.save(policy);
    }


    public void enableAudit(String tableName) {
        String sql = "AUDIT SELECT, INSERT, UPDATE, DELETE ON " + tableName;
        jdbcTemplate.execute(sql);
    }

    public void configureVPD(String tableName, String policyFunction) {
        // First drop existing policy if it exists
        String dropPolicySQL = "BEGIN " +
                "DBMS_RLS.DROP_POLICY(" +
                "  object_schema => USER, " +
                "  object_name => ?, " +
                "  policy_name => ? " +
                "); EXCEPTION WHEN OTHERS THEN NULL; END;";

        jdbcTemplate.update(dropPolicySQL, tableName, tableName + "_policy");

        // Create function and add policy (rest of your existing code)
        String checkFunctionSQL = "SELECT COUNT(*) FROM USER_OBJECTS WHERE OBJECT_TYPE = 'FUNCTION' AND OBJECT_NAME = ?";
        Integer functionCount = jdbcTemplate.queryForObject(checkFunctionSQL, Integer.class, policyFunction);

        if (functionCount == null || functionCount == 0) {
            String functionCreationSQL = "CREATE OR REPLACE FUNCTION " + policyFunction +
                    " (schema_name IN VARCHAR2, table_name IN VARCHAR2) " +
                    "RETURN VARCHAR2 AS BEGIN RETURN 'id = 1'; END;";
            jdbcTemplate.execute(functionCreationSQL);
        }

        String vpdSQL = "BEGIN DBMS_RLS.ADD_POLICY(" +
                "object_schema => USER, " +
                "object_name => ?, " +
                "policy_name => ?, " +
                "function_schema => USER, " +
                "policy_function => ?); END;";

        jdbcTemplate.update(vpdSQL, tableName, tableName + "_policy", policyFunction);
    }
    }



