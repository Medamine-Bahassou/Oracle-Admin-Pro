package ma.fstt.lsi.oracle.dto.surveillance;

import lombok.Data;

@Data
public class PerformanceMetricsDTO {
    private double cpuUsage;
    private double memoryUsage;
    private double ioUsage;
    private long timestamp;
}
