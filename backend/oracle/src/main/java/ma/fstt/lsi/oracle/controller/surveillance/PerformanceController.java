package ma.fstt.lsi.oracle.controller.surveillance;


import ma.fstt.lsi.oracle.dto.surveillance.ASHReportDTO;
import ma.fstt.lsi.oracle.dto.surveillance.AWRReportDTO;
import ma.fstt.lsi.oracle.dto.surveillance.PerformanceMetricsDTO;
import ma.fstt.lsi.oracle.service.surveillance.PerformanceMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "*")
public class PerformanceController {

    @Autowired
    private PerformanceMonitorService performanceService;

    @GetMapping("/metrics")
    public PerformanceMetricsDTO getCurrentMetrics() {
        return performanceService.getCurrentMetrics();
    }

    @GetMapping("/awr")
    public List<AWRReportDTO> getAWRReport() {
        return performanceService.getAWRReport();
    }

    @GetMapping("/ash")
    public List<ASHReportDTO> getASHReport() {
        return performanceService.getASHReport();
    }
}
