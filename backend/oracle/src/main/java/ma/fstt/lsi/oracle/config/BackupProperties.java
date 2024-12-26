package ma.fstt.lsi.oracle.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "backup.rman")
@Getter
@Setter
public class BackupProperties {
    private String path;
    private Integer retentionDays;
}
