package ma.fstt.lsi.oracle.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
public class BackupSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Type de récurrence
    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

    // Heure d'exécution
    private LocalTime executionTime;

    // Pour la récurrence hebdomadaire
    @ElementCollection
    private Set<DayOfWeek> selectedDays;

    // Pour la récurrence mensuelle
    private Integer dayOfMonth;

    private String backupType; // FULL or INCREMENTAL
    private int incrementalLevel; // 0 or 1
    private boolean active;

    // Champ généré pour Spring Scheduler
    private String cronExpression;

    public enum RecurrenceType {
        DAILY,
        WEEKLY,
        MONTHLY
    }

    // Constructeur pour planning quotidien
    public BackupSchedule(LocalTime executionTime, String backupType, int incrementalLevel) {
        this.recurrenceType = RecurrenceType.DAILY;
        this.executionTime = executionTime;
        this.backupType = backupType;
        this.incrementalLevel = incrementalLevel;
        this.active = true;
        generateCronExpression();
    }

    // Constructeur pour planning hebdomadaire
    public BackupSchedule(LocalTime executionTime, Set<DayOfWeek> selectedDays, String backupType, int incrementalLevel) {
        this.recurrenceType = RecurrenceType.WEEKLY;
        this.executionTime = executionTime;
        this.selectedDays = selectedDays;
        this.backupType = backupType;
        this.incrementalLevel = incrementalLevel;
        this.active = true;
        generateCronExpression();
    }

    // Constructeur pour planning mensuel
    public BackupSchedule(LocalTime executionTime, int dayOfMonth, String backupType, int incrementalLevel) {
        this.recurrenceType = RecurrenceType.MONTHLY;
        this.executionTime = executionTime;
        this.dayOfMonth = dayOfMonth;
        this.backupType = backupType;
        this.incrementalLevel = incrementalLevel;
        this.active = true;
        generateCronExpression();
    }

    private void generateCronExpression() {
        // Format: second minute hour day-of-month month day-of-week
        switch (recurrenceType) {
            case DAILY:
                this.cronExpression = String.format("0 %d %d * * *",
                        executionTime.getMinute(),
                        executionTime.getHour());
                break;

            case WEEKLY:
                String days = selectedDays.stream()
                        .map(day -> String.valueOf(day.getValue() % 7)) // Convertit dimanche de 7 à 0
                        .collect(Collectors.joining(","));
                this.cronExpression = String.format("0 %d %d * * %s",
                        executionTime.getMinute(),
                        executionTime.getHour(),
                        days);
                break;

            case MONTHLY:
                this.cronExpression = String.format("0 %d %d %d * *",
                        executionTime.getMinute(),
                        executionTime.getHour(),
                        dayOfMonth);
                break;
        }
    }
}