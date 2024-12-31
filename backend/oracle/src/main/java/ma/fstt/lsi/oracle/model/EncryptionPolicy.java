package ma.fstt.lsi.oracle.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "encryption_policies")
public class EncryptionPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tableName;
    private String columnName;
    private String encryptionType;
    private boolean enabled;

    // Getters and setters
}