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
        String sql = "BEGIN " +
                "DBMS_RLS.ADD_POLICY(" +
                "  object_schema => USER, " +
                "  object_name => '" + tableName + "', " +
                "  policy_name => '" + tableName + "_policy', " +
                "  function_schema => USER, " +
                "  policy_function => '" + policyFunction + "', " +
                "  statement_types => 'SELECT,UPDATE,DELETE,INSERT'" +
                ");" +
                "END;";
        jdbcTemplate.execute(sql);
    }

}