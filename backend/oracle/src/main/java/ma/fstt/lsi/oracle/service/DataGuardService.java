package ma.fstt.lsi.oracle.service;

import jakarta.persistence.*;
import ma.fstt.lsi.oracle.dao.DataGuardConfigRepository;
import ma.fstt.lsi.oracle.model.DataGuardConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DataGuardService {

    @Autowired
    private DataGuardConfigRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<DataGuardConfig> getAllConfigs() {
        return repository.findAll();
    }

    public DataGuardConfig saveConfig(DataGuardConfig config) {
        return repository.save(config);
    }

    public void updateDataGuardStatus() {
        String sql = "SELECT DATABASE_ROLE, PROTECTION_MODE, TRANSPORT_LAG, APPLY_LAG FROM V$DATABASE";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);

        DataGuardConfig config = repository.findAll().get(0); // Assuming only one config
        config.setStatus((String) result.get("DATABASE_ROLE"));
        config.setProtectionMode((String) result.get("PROTECTION_MODE"));
        config.setTransportLag((String) result.get("TRANSPORT_LAG"));
        config.setApplyLag((String) result.get("APPLY_LAG"));
        config.setLastSync(LocalDateTime.now());

        repository.save(config);
    }

    public void simulateSwitchover() {
        jdbcTemplate.execute("ALTER DATABASE COMMIT TO SWITCHOVER TO PHYSICAL STANDBY WITH SESSION SHUTDOWN");
        updateDataGuardStatus();
    }

    public void simulateFailover() {
        jdbcTemplate.execute("ALTER DATABASE ACTIVATE PHYSICAL STANDBY DATABASE");
        updateDataGuardStatus();
    }

    public void simulateFailback() {
        jdbcTemplate.execute("ALTER DATABASE ACTIVATE PHYSICAL STANDBY DATABASE");
        updateDataGuardStatus();
    }

    public String getAvailabilityReport() {
        String sql = "SELECT AVAILABILITY_PERCENTAGE FROM V$DATAGUARD_STATS WHERE NAME = 'transport lag'";
        Double availability = jdbcTemplate.queryForObject(sql, Double.class);
        return String.format("Availability Report: %.2f%% uptime in the last 24 hours", availability);
    }
}
