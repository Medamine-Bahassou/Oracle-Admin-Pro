package ma.fstt.lsi.oracle.controller.optimisation;

import lombok.RequiredArgsConstructor;
import ma.fstt.lsi.oracle.model.SlowQuery;
import ma.fstt.lsi.oracle.model.StatisticsJob;
import ma.fstt.lsi.oracle.service.optimisation.PerformanceOptimizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // Add this
public class PerformanceOptimizationController {

    private final PerformanceOptimizationService performanceService;

    @GetMapping("/slow-queries")
    public ResponseEntity<List<SlowQuery>> getSlowQueries() {
        return ResponseEntity.ok(performanceService.identifySlowQueries());
    }

    @PostMapping("/optimize-query/{queryId}")
    public ResponseEntity<SlowQuery> optimizeQuery(@PathVariable Long queryId) {
        return ResponseEntity.ok(performanceService.optimizeQuery(queryId));
    }

    @PostMapping("/schedule-statistics")
    public ResponseEntity<?> scheduleStatistics(@RequestBody StatisticsJob job) {
        try {
            StatisticsJob scheduledJob = performanceService.scheduleStatisticsGathering(job);
            return ResponseEntity.ok(scheduledJob);
        } catch (IllegalArgumentException e) {
            // Return a bad request response with the error message
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
}