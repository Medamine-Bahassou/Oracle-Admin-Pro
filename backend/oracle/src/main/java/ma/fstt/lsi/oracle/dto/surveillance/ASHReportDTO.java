package ma.fstt.lsi.oracle.dto.surveillance;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ASHReportDTO {
    private String sessionId;
    private String sqlId;
    private String event;
    private String waitClass;
    private Timestamp sampleTime;
}
