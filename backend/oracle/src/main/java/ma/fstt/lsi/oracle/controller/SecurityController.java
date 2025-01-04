package ma.fstt.lsi.oracle.controller;

import ma.fstt.lsi.oracle.service.DatabaseSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security")
public class SecurityController {
    @Autowired
    private DatabaseSecurityService securityService;

    @PostMapping("/tde/enable")
    public ResponseEntity<?> enableTDE(@RequestParam String tableName,
                                       @RequestParam String columnName) {
        try {
            securityService.enableTDE(tableName, columnName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error enabling TDE: " + e.getMessage());
        }
    }

    @PostMapping("/audit/enable")
    public ResponseEntity<?> enableAudit(@RequestParam String tableName) {
        try {
            securityService.enableAudit(tableName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error enabling audit: " + e.getMessage());
        }
    }

    @PostMapping("/vpd/configure")
    public ResponseEntity<?> configureVPD(@RequestParam String tableName,
                                          @RequestParam String policyFunction) {
        try {
            securityService.configureVPD(tableName, policyFunction);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error configuring VPD: " + e.getMessage());
        }
    }
}
