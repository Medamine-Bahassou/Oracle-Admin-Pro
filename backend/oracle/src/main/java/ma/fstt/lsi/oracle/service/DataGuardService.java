package ma.fstt.lsi.oracle.service;

import ma.fstt.lsi.oracle.dto.DataGuardConfigRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataGuardService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void configureDataGuard(DataGuardConfigRequest request) {
        String sql = "ALTER SYSTEM SET log_archive_dest_1 = 'LOCATION=" + request.getArchiveDest() + "'";
        jdbcTemplate.execute(sql);
    }
    public void initiateFailover() {
        String sql = "ALTER DATABASE RECOVER MANAGED STANDBY DATABASE FINISH FORCE";
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            // Log the error and include actionable details
            throw new RuntimeException("Failover failed: Please ensure you are connected to a standby database. Error: " + e.getMessage(), e);
        }
    }



    @Scheduled(fixedRate = 60000)
    public Map<String, String> getDataGuardStatus() {
        String sql = "SELECT * FROM V$DATAGUARD_STATS";
        Map<String, String> status = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            status.put("NAME", rs.getString("NAME"));
            status.put("VALUE", rs.getString("VALUE"));
        });

        return status;
    }

    public String generateAvailabilityReport() {
        String sql = "SELECT * FROM V$DATAGUARD_STATS";
        StringBuilder report = new StringBuilder("Data Guard Availability Report:\n");

        jdbcTemplate.query(sql, rs -> {
            report.append(rs.getString("NAME"))
                    .append(": ")
                    .append(rs.getString("VALUE"))
                    .append("\n");
        });

        return report.toString();
    }
}