package ma.fstt.lsi.oracle.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RestoreStatus {
    private Long backupId;
    private boolean completed;
    private int progress;
    private List<String> logs;
    private String error;

    public RestoreStatus(Long backupId) {
        this.backupId = backupId;
        this.completed = false;
        this.progress = 0;
        this.logs = new ArrayList<>();
    }

    public void addLog(String log) {
        this.logs.add(log);
    }

    public void addProgress(int increment) {
        this.progress = Math.min(100, this.progress + increment);
    }
}