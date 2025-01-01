package ma.fstt.lsi.oracle.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/ORCL");
        config.setUsername("C##USER");
        config.setPassword("password");
        config.setDriverClassName("oracle.jdbc.OracleDriver");

        // Pool Configuration
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(120000);

        // Connection Test
        config.setConnectionTestQuery("SELECT 1 FROM DUAL");
        config.setPoolName("OracleHikariPool");

        return new HikariDataSource(config);
    }

}
