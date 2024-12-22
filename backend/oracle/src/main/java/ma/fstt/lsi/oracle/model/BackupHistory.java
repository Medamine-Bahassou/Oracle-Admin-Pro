package ma.fstt.lsi.oracle.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Data
@Table(name = "BACKUP_HISTORY")
public class BackupHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // COMPLETE or INCREMENTAL

    private String status; // SUCCESS or FAILURE

    private LocalDateTime backupDate;

    private String backupLocation;

    private LocalDateTime restoreDate;
}