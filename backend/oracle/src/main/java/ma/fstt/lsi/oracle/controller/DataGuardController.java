package ma.fstt.lsi.oracle.controller;

import ma.fstt.lsi.oracle.dto.DataGuardConfigRequest;
import ma.fstt.lsi.oracle.service.DataGuardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dataguard")
class DataGuardController {

    @Autowired
    private DataGuardService dataGuardService;

    @PostMapping("/configure")
    public ResponseEntity<String> configureDataGuard(@RequestBody DataGuardConfigRequest request) {
        try {
            dataGuardService.configureDataGuard(request);
            return ResponseEntity.ok("Data Guard configured successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Configuration failed: " + e.getMessage());
        }
    }

    @PostMapping("/failover")
    public ResponseEntity<String> simulateFailover() {
        try {
            dataGuardService.initiateFailover();
            return ResponseEntity.ok("Failover initiated successfully. The standby database is now the primary database.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failover failed: " + e.getMessage() + ". Verify the standby database role and control file.");
        }
    }


    @GetMapping("/monitor")
    public ResponseEntity<Map<String, String>> monitorDataGuard() {
        try {
            return ResponseEntity.ok(dataGuardService.getDataGuardStatus());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve status: " + e.getMessage()));
        }
    }

    @GetMapping("/report")
    public ResponseEntity<String> generateReport() {
        try {
            String report = dataGuardService.generateAvailabilityReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Report generation failed: " + e.getMessage());
        }
    }
}