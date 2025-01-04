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
     * @param role     the role to assign
     */
    @Transactional(rollbackOn = Exception.class)
    public void assignRoleToUser(String username, String role) {
        String sql;
        if (isCustomRole(role)) {
            sql = String.format("GRANT c##"+"%s TO c##"+"%s", role, username);
        } else {
            sql = String.format("GRANT %s TO c##"+"%s", role, username);
        }
        jdbcTemplate.execute(sql);

    }

    /**
     * Creates a new role
     *
     * @param roleName The name of the role to create
     */
    @Transactional(rollbackOn = Exception.class)
    public void createRole(String roleName) {
        String createRoleSql = String.format("CREATE ROLE c##%s", roleName);
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
        if (privileges == null || privileges.length == 0) {
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
     * @param roleName   the name of the role to create
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
     * Revoke a privilege from a user.
     *
     * @param username the username from whom the privilege will be revoked
     * @param privilege the privilege to revoke
     */
    @Transactional(rollbackOn = Exception.class)
    public void revokeRoleFromUser(String username, String privilege) { // Changed role to privilege
        String sql = String.format("REVOKE %s FROM C##" + "%s", privilege.toUpperCase(), username.toUpperCase()); // changed the method to revoke privileges instead of role
        jdbcTemplate.execute(sql);
    }

    /**
     * Drop a role from the database.
     *
     * @param roleName the name of the role to drop
     */
    @Transactional(rollbackOn = Exception.class)
    public void dropRole(String roleName) {
        String sql = String.format("DROP ROLE c##%s", roleName);
        jdbcTemplate.execute(sql);
    }

    /**
     * Get all roles in the database.
     *
     * @return List of roles
     */
    public List<Map<String, Object>> getAllRoles() {
        String sql = "SELECT ROLE FROM DBA_ROLES";
        return jdbcTemplate.queryForList(sql);
    }


    /**
     * Get privileges assigned to a specific user.
     *
     * @param username the username to check
     * @return List of privileges assigned to the user
     */
    public List<Map<String, Object>> getUserPrivileges(String username) {
        String sql = """
            SELECT PRIVILEGE
            FROM sys.dba_sys_privs
            WHERE grantee = ?
            UNION
            SELECT PRIVILEGE
            FROM dba_role_privs rp JOIN role_sys_privs rsp ON (rp.granted_role = rsp.role)
            WHERE rp.grantee = ?
            ORDER BY 1
            """;
        String prefixedUsername = "C##" + username.toUpperCase(); // Prefix user name
        return jdbcTemplate.queryForList(sql, prefixedUsername, prefixedUsername);
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

    /**
     * Get roles assigned to a specific user.
     *
     * @param username the username to check
     * @return List of roles assigned to the user
     */
    public List<Map<String, Object>> getUserRoles(String username) {
        String sql = "SELECT granted_role FROM dba_role_privs WHERE grantee = ?";
        String prefixedUsername = "C##" + username.toUpperCase();
        return jdbcTemplate.queryForList(sql, prefixedUsername);
    }
    private boolean isCustomRole(String role) {
        String sql = "SELECT COUNT(*) FROM DBA_ROLES WHERE ROLE = ?";
        int count =  jdbcTemplate.queryForObject(sql, Integer.class, "C##"+role.toUpperCase());
        return count > 0;
    }
}