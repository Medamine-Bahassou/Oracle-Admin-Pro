package ma.fstt.lsi.oracle.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    private final JdbcTemplate jdbcTemplate;

    public RoleService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Assign a role to a user.
     *
     * @param username the username to whom the role will be assigned
     * @param role the role to assign
     */
    @Transactional(rollbackOn = Exception.class)
    public void assignRoleToUser(String username, String role) {
        String sql = String.format("GRANT c##"+"%s TO c##"+"%s", role, username);
        jdbcTemplate.execute(sql);
    }

    /**
     * Creates a new role
     * @param roleName The name of the role to create
     */
    @Transactional(rollbackOn = Exception.class)
    public void createRole(String roleName){
        String createRoleSql = String.format("CREATE ROLE c##"+"%s", roleName);
        jdbcTemplate.execute(createRoleSql);
    }

    /**
     * Grant privileges to an existing role.
     *
     * @param roleName   the name of the role to grant privileges to
     * @param privileges the privileges to grant to the role
     */
    @Transactional(rollbackOn = Exception.class)
    public void grantPrivilegesToRole(String roleName, String[] privileges) {
        if(privileges == null || privileges.length == 0){
            return;
        }
        String prefixedRoleName = "c##" + roleName;
        for (String privilege : privileges) {
            String grantPrivilegeSql = String.format("GRANT %s TO %s", privilege, prefixedRoleName);
            jdbcTemplate.execute(grantPrivilegeSql);
        }
    }


    /**
     * Create a new role with specified privileges.
     *
     * @param roleName the name of the role to create
     * @param privileges the privileges to grant to the role
     */
    @Transactional(rollbackOn = Exception.class)
    public void createRoleWithPrivileges(String roleName, String[] privileges) {
        // Create role
        createRole(roleName);
        // Grant privileges to the role
        grantPrivilegesToRole(roleName, privileges);
    }
    /**
     * Revoke a role from a user.
     *
     * @param username the username from whom the role will be revoked
     * @param role the role to revoke
     */
    @Transactional(rollbackOn = Exception.class)
    public void revokeRoleFromUser(String username, String role) {
        String sql = String.format("REVOKE %s FROM %s", role, username);
        jdbcTemplate.execute(sql);
    }

    /**
     * Drop a role from the database.
     *
     * @param roleName the name of the role to drop
     */
    @Transactional(rollbackOn = Exception.class)
    public void dropRole(String roleName) {
        String sql = String.format("DROP ROLE c##"+"%s", roleName);
        jdbcTemplate.execute(sql);
    }

    /**
     * Get all roles in the database.
     *
     * @return List of roles
     */
    public List<Map<String, Object>> getAllRoles() {
        String sql = "SELECT * FROM DBA_ROLES WHERE ROLE LIKE 'C##%'";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Get roles assigned to a specific user.
     *
     * @param username the username to check
     * @return List of roles assigned to the user
     */
    public List<Map<String, Object>> getUserRoles(String username) {
        String sql = "SELECT GRANTED_ROLE FROM DBA_ROLE_PRIVS WHERE GRANTEE = ?";
        return jdbcTemplate.queryForList(sql, username.toUpperCase());
    }

    /**
     * Get privileges assigned to a specific role.
     *
     * @param roleName the role name to check
     * @return List of privileges assigned to the role
     */
    public List<Map<String, Object>> getRolePrivileges(String roleName) {
        String sql = "SELECT PRIVILEGE FROM ROLE_SYS_PRIVS WHERE ROLE = ?";
        return jdbcTemplate.queryForList(sql, roleName.toUpperCase());
    }
}