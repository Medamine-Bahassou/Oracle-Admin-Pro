package ma.fstt.lsi.oracle.controller;

import ma.fstt.lsi.oracle.model.DataGuardConfig;
import ma.fstt.lsi.oracle.service.DataGuardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dataguard")
public class DataGuardController {

    @Autowired
    private DataGuardService service;

    @GetMapping("/configs")
    public List<DataGuardConfig> getAllConfigs() {
        return service.getAllConfigs();
    }

    @PostMapping("/configs")
    public DataGuardConfig saveConfig(@RequestBody DataGuardConfig config) {
        return service.saveConfig(config);
    }

    @PostMapping("/update-status")
    public void updateStatus() {
        service.updateDataGuardStatus();
    }

    @PostMapping("/simulate/switchover")
    public void simulateSwitchover() {
        service.simulateSwitchover();
    }

    @PostMapping("/simulate/failover")
    public void simulateFailover() {
        service.simulateFailover();
    }

    @PostMapping("/simulate/failback")
    public void simulateFailback() {
        service.simulateFailback();
    }

    @GetMapping("/report")
    public String getAvailabilityReport() {
        return service.getAvailabilityReport();
    }
}

