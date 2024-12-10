package ma.fstt.lsi.oracle.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "backup_logs")
public class BackupLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String operation; // Backup or Restore

    private String status; // Success or Failure

    private LocalDateTime executedAt;

    private String details; // Any error or success messages
}
