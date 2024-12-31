package ma.fstt.lsi.oracle.dto.surveillance;

import lombok.Data;

@Data
public class SQLStatDTO {
    private String sqlId;
    private String sqlText;
    private long executions;
    private double elapsedTime;
    private double cpuTime;
    private double bufferGets;
    private double diskReads;
    private double rowsProcessed;
    private String planHashValue;
}
