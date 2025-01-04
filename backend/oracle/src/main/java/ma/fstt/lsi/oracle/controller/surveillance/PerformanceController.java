package ma.fstt.lsi.oracle.controller.surveillance;

import ma.fstt.lsi.oracle.dto.surveillance.ASHReportDTO;
import ma.fstt.lsi.oracle.dto.surveillance.AWRReportDTO;
import ma.fstt.lsi.oracle.dto.surveillance.PerformanceMetricsDTO;
import ma.fstt.lsi.oracle.service.surveillance.PerformanceMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance")
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
    @GetMapping("/awr-chart")
    public Map<String, List<Map<String, Object>>> getAWRChartData(@RequestParam(required = false) String sqlIdFilter) {
        return performanceService.getAWRChartData(sqlIdFilter);
    }
    @GetMapping("/ash")
    public List<ASHReportDTO> getASHReport() {
        return performanceService.getASHReport();
    }
    @GetMapping("/ash-chart")
    public Map<String, Long> getASHChartData(@RequestParam(required = false) String eventFilter) {
        return performanceService.getASHChartData(eventFilter);
    }
}