package ma.fstt.lsi.oracle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@Table(name = "BACKUP_HISTORY")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BackupHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // COMPLETE or INCREMENTAL

    private String status; // SUCCESS or FAILURE

    private LocalDateTime backupDate;

    private String backupLocation;

    private LocalDateTime restoreDate;

    public BackupHistory(String type, String status, LocalDateTime backupDate, String backupLocation) {
        this.type = type;
        this.status = status;
        this.backupDate = backupDate;
        this.backupLocation = backupLocation;
        this.restoreDate = null;  // You can set the restore date when the restore process happens
    }
}