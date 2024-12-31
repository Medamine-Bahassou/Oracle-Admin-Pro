package ma.fstt.lsi.oracle.service;

import lombok.RequiredArgsConstructor;
import ma.fstt.lsi.oracle.dao.UserDao;
import ma.fstt.lsi.oracle.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        try {
            // Check if user already exists in Oracle DB
            String checkUserSql = "SELECT COUNT(*) FROM ALL_USERS WHERE USERNAME = ?";
            int userCount = jdbcTemplate.queryForObject(checkUserSql,
                    new Object[]{user.getUsername().toUpperCase()}, Integer.class);

            if (userCount > 0) {
                throw new RuntimeException("Oracle user '" + user.getUsername() + "' already exists.");
            }

            // Create Oracle user
            String createUserSql = String.format(
                    "CREATE USER c##"+"%s IDENTIFIED BY %s",
                    user.getUsername(),
                    user.getPassword()
            );
            jdbcTemplate.execute(createUserSql);

            user.setCreatedDate(LocalDateTime.now());
            return userRepository.saveAndFlush(user);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating Oracle user", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public User updateUser(Long id, User updatedUser) {
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update password if changed
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().equals(existingUser.getPassword())) {
                String alterPasswordSql = String.format(
                        "ALTER USER %s IDENTIFIED BY %s",
                        existingUser.getUsername(),
                        updatedUser.getPassword()
                );
                jdbcTemplate.execute(alterPasswordSql);
                existingUser.setPassword(updatedUser.getPassword());
            }

            // Update default tablespace if changed
            if (updatedUser.getDefaultTablespace() != null &&
                    !updatedUser.getDefaultTablespace().equals(existingUser.getDefaultTablespace())) {
                String alterDefaultTablespaceSql = String.format(
                        "ALTER USER %s DEFAULT TABLESPACE %s",
                        existingUser.getUsername(),
                        updatedUser.getDefaultTablespace()
                );
                jdbcTemplate.execute(alterDefaultTablespaceSql);
                existingUser.setDefaultTablespace(updatedUser.getDefaultTablespace());
            }

            // Update temporary tablespace if changed
            if (updatedUser.getTemporaryTablespace() != null &&
                    !updatedUser.getTemporaryTablespace().equals(existingUser.getTemporaryTablespace())) {
                String alterTemporaryTablespaceSql = String.format(
                        "ALTER USER %s TEMPORARY TABLESPACE %s",
                        existingUser.getUsername(),
                        updatedUser.getTemporaryTablespace()
                );
                jdbcTemplate.execute(alterTemporaryTablespaceSql);
                existingUser.setTemporaryTablespace(updatedUser.getTemporaryTablespace());
            }

            // Update quota if changed
            if (updatedUser.getQuota() != null &&
                    !updatedUser.getQuota().equals(existingUser.getQuota())) {
                String quotaSql = String.format(
                        "ALTER USER %s QUOTA %s ON %s",
                        existingUser.getUsername(),
                        updatedUser.getQuota(),
                        existingUser.getDefaultTablespace() // Using existing tablespace for quota
                );
                jdbcTemplate.execute(quotaSql);
                existingUser.setQuota(updatedUser.getQuota());
            }

            // Update last modified date
            existingUser.setLastModifiedDate(LocalDateTime.now());

            return userRepository.saveAndFlush(existingUser);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException("The user was modified by another transaction. Please try again.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }



    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        String dropUserSql = String.format("DROP USER %s CASCADE", user.getUsername());
        jdbcTemplate.execute(dropUserSql);
        userRepository.deleteById(id);
    }
}
