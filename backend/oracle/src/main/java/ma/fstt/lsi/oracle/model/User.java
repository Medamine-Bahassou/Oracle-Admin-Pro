package ma.fstt.lsi.oracle.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "oracle_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String defaultTablespace;
    private String temporaryTablespace;
    private String profile;
    private String quota;
    private boolean accountLocked;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}

