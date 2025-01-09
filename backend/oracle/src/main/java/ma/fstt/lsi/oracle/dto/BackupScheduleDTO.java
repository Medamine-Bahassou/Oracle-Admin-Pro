package ma.fstt.lsi.oracle.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Data
public class BackupScheduleDTO {
    private RecurrenceType recurrenceType;
    private LocalTime executionTime;
    private Set<DayOfWeek> selectedDays;
    private Integer dayOfMonth;
    private String backupType;
    private int incrementalLevel;

    public enum RecurrenceType {
        DAILY,
        WEEKLY,
        MONTHLY
    }
}