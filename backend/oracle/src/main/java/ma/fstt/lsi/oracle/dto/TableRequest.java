package ma.fstt.lsi.oracle.dto;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class TableRequest {
    // Getters and Setters
    private String tableName;
    private List<String> columns;


}
