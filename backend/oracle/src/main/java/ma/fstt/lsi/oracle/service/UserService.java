package ma.fstt.lsi.oracle.service;

import lombok.RequiredArgsConstructor;
import ma.fstt.lsi.oracle.dao.UserDao;
import ma.fstt.lsi.oracle.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public User createUser(User user) {
        try {
            // Create Oracle user
            String createUserSql = String.format(
                    "CREATE USER %s IDENTIFIED BY %s DEFAULT TABLESPACE %s TEMPORARY TABLESPACE %s",
                    user.getUsername(),
                    user.getPassword(),
                    user.getDefaultTablespace(),
                    user.getTemporaryTablespace()
            );
            jdbcTemplate.execute(createUserSql);

            // Set quota if specified
            if (user.getQuota() != null) {
                String quotaSql = String.format(
                        "ALTER USER %s QUOTA %s ON %s",
                        user.getUsername(),
                        user.getQuota(),
                        user.getDefaultTablespace()
                );
                jdbcTemplate.execute(quotaSql);
            }

            user.setCreatedDate(LocalDateTime.now());
            return userRepository.save(user);
        } catch (Exception e) {
            // Log the exception and rethrow
            e.printStackTrace();
            throw new RuntimeException("Error while creating Oracle user", e);
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
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        // Update Oracle user properties
        String alterUserSql = String.format(
                "ALTER USER %s IDENTIFIED BY %s DEFAULT TABLESPACE %s TEMPORARY TABLESPACE %s",
                existingUser.getUsername(),
                updatedUser.getPassword(),
                updatedUser.getDefaultTablespace(),
                updatedUser.getTemporaryTablespace()
        );

        jdbcTemplate.execute(alterUserSql);

        // Update quota if changed
        if (!existingUser.getQuota().equals(updatedUser.getQuota())) {
            String quotaSql = String.format(
                    "ALTER USER %s QUOTA %s ON %s",
                    existingUser.getUsername(),
                    updatedUser.getQuota(),
                    updatedUser.getDefaultTablespace()
            );
            jdbcTemplate.execute(quotaSql);
        }

        // Update entity
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setDefaultTablespace(updatedUser.getDefaultTablespace());
        existingUser.setTemporaryTablespace(updatedUser.getTemporaryTablespace());
        existingUser.setQuota(updatedUser.getQuota());
        existingUser.setLastModifiedDate(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        String dropUserSql = String.format("DROP USER %s CASCADE", user.getUsername());
        jdbcTemplate.execute(dropUserSql);
        userRepository.deleteById(id);
    }
}
