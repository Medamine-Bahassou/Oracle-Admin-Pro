package ma.fstt.lsi.oracle.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "SLOW_QUERIES")
@Data
public class SlowQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SQL_ID")
    private String sqlId;

    @Column(name = "SQL_TEXT", length = 4000)
    private String sqlText;

    @Column(name = "ELAPSED_TIME")
    private Double elapsedTime;

    @Column(name = "CPU_TIME")
    private Double cpuTime;

    @Column(name = "EXECUTIONS")
    private Integer executions;

    @Column(name = "CAPTURE_TIME")
    private LocalDateTime captureTime;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "OPTIMIZATION_RECOMMENDATIONS", length = 4000)
    private String optimizationRecommendations;
}