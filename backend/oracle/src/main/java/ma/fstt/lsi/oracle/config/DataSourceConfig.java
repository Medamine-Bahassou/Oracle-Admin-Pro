package ma.fstt.lsi.oracle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() throws SQLException {
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setURL("jdbc:oracle:thin:@localhost:1522:orcl1");
        dataSource.setUser("c##user"); // Use the SYSDBA privilege
        dataSource.setPassword("password"); // Replace with your SYS password
        return dataSource;
    }
}
