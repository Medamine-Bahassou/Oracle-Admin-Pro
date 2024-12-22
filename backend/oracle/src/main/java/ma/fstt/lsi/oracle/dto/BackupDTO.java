package ma.fstt.lsi.oracle.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BackupDTO {
    private String backupId;
    private String backupType; // FULL or INCREMENTAL
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String backupLocation;
}
