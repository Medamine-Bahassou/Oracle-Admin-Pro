package ma.fstt.lsi.oracle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_guard_config")
@Data
public class DataGuardConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String primaryDatabase;

    @Column(nullable = false)
    private String standbyDatabase;

    @Column(nullable = false)
    private String status;

    @Column(name = "last_sync")
    private LocalDateTime lastSync;

    @Column(name = "protection_mode")
    private String protectionMode;

    @Column(name = "transport_lag")
    private String transportLag;

    @Column(name = "apply_lag")
    private String applyLag;
}