package ma.fstt.lsi.oracle.dto.surveillance;
import lombok.Data;

import java.util.List;

@Data
public class AWRReportDTO {
    private String snapshotId;
    private String startTime;
    private String endTime;
    private double dbTime;
    private double dbCpuTime;
    private List<SQLStatDTO> topSQLStats;
}
