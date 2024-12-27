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
        // Check if the policy function already exists
        String checkFunctionSQL = "SELECT COUNT(*) FROM USER_OBJECTS WHERE OBJECT_TYPE = 'FUNCTION' AND OBJECT_NAME = ?";
        Integer count = jdbcTemplate.queryForObject(checkFunctionSQL, Integer.class, policyFunction);

        if (count == null || count == 0) {
            // Function doesn't exist, so create it
            String functionCreationSQL = "CREATE OR REPLACE FUNCTION " + policyFunction + " (schema_name IN VARCHAR2, table_name IN VARCHAR2) " +
                    "RETURN VARCHAR2 AS BEGIN " +
                    "    -- Dynamically get ID, for example from a session variable or custom logic " +
                    "    RETURN 'id = 1'; " +  // Example: restrict rows where id = 1 dynamically
                    "END;";
            jdbcTemplate.execute(functionCreationSQL);
        }

        // Now add the VPD policy dynamically
        String vpdSQL = "BEGIN " +
                "DBMS_RLS.ADD_POLICY(" +
                "  object_schema => USER, " +
                "  object_name => ?, " +
                "  policy_name => ? , " +
                "  function_schema => USER, " +
                "  policy_function => ? " +
                ");" +
                "END;";

        // Execute the VPD policy creation with parameterized values
        jdbcTemplate.update(vpdSQL, tableName, tableName + "_policy", policyFunction);
    }

}