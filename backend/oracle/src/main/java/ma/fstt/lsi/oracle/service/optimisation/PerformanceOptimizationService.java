package ma.fstt.lsi.oracle.service.optimisation;

import lombok.RequiredArgsConstructor;
import ma.fstt.lsi.oracle.model.SlowQuery;
import ma.fstt.lsi.oracle.model.StatisticsJob;
import ma.fstt.lsi.oracle.dao.SlowQueryRepository;
import ma.fstt.lsi.oracle.dao.StatisticsJobRepository;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceOptimizationService {

    private final JdbcTemplate jdbcTemplate;
    private final SlowQueryRepository slowQueryRepository;
    private final StatisticsJobRepository statisticsJobRepository;
    private static final Logger logger = LoggerFactory.getLogger(PerformanceOptimizationService.class);

    @Transactional
    public List<SlowQuery> identifySlowQueries() {
        try {
            String sql = """
                SELECT sql_id, sql_text, elapsed_time, cpu_time, executions
                FROM v$sql
                WHERE elapsed_time > 1000000
                ORDER BY elapsed_time DESC
                FETCH FIRST 10 ROWS ONLY
            """;

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

            for (Map<String, Object> row : results) {
                SlowQuery query = new SlowQuery();
                query.setSqlId((String) row.get("sql_id"));
                query.setSqlText((String) row.get("sql_text"));

                // Handle BigDecimal and other numeric types correctly
                query.setElapsedTime(((Number) row.get("elapsed_time")).doubleValue());
                query.setCpuTime(((Number) row.get("cpu_time")).doubleValue());

                // Convert executions to Integer safely
                Object executionsObj = row.get("executions");
                query.setExecutions(executionsObj != null ? ((Number) executionsObj).intValue() : 0);

                query.setCaptureTime(LocalDateTime.now());
                query.setStatus("IDENTIFIED");

                slowQueryRepository.save(query);
                logger.info("Identified slow query with SQL ID: " + query.getSqlId());
            }

            return slowQueryRepository.findByStatusOrderByElapsedTimeDesc("IDENTIFIED");

        } catch (Exception e) {
            logger.error("Error identifying slow queries: " + e.getMessage(), e);
            throw new RuntimeException("Error identifying slow queries", e);
        }
    }

    @Transactional
    public SlowQuery optimizeQuery(Long queryId) {
        SlowQuery query = slowQueryRepository.findById(queryId)
                .orElseThrow(() -> new RuntimeException("Query not found"));

        String recommendations = null;

        try {
            recommendations = jdbcTemplate.execute((ConnectionCallback<String>) connection -> {
                try (CallableStatement dropTask = connection.prepareCall(
                        "BEGIN " +
                                "  BEGIN " +
                                "    DBMS_SQLTUNE.DROP_TUNING_TASK('TUNE_' || ?);" +
                                "  EXCEPTION " +
                                "    WHEN OTHERS THEN NULL;" +
                                "  END;" +
                                "END;"
                )) {
                    dropTask.setString(1, query.getSqlId());
                    dropTask.execute();
                }

                // Create tuning task with correct parameters
                try (CallableStatement createTask = connection.prepareCall(
                        "DECLARE " +
                                "  l_sql_tune_task_id VARCHAR2(100);" +
                                "BEGIN " +
                                "  l_sql_tune_task_id := DBMS_SQLTUNE.CREATE_TUNING_TASK(" +
                                "    sql_id => ?, " +
                                "    task_name => 'TUNE_' || ?, " +
                                "    time_limit => 3600" +
                                "  );" +
                                "END;"
                )) {
                    createTask.setString(1, query.getSqlId());
                    createTask.setString(2, query.getSqlId());
                    createTask.execute();
                }

                // Execute tuning task
                try (CallableStatement executeTask = connection.prepareCall(
                        "BEGIN " +
                                "  DBMS_SQLTUNE.EXECUTE_TUNING_TASK(" +
                                "    task_name => 'TUNE_' || ?" +
                                ");" +
                                "END;"
                )) {
                    executeTask.setString(1, query.getSqlId());
                    executeTask.execute();
                }

                // Retrieve recommendations
                try (CallableStatement getRecommendations = connection.prepareCall(
                        "BEGIN " +
                                "  ? := DBMS_SQLTUNE.REPORT_TUNING_TASK(" +
                                "    task_name => 'TUNE_' || ?, " +
                                "    type => 'TEXT', " +
                                "    level => 'TYPICAL'" +
                                ");" +
                                "END;"
                )) {
                    getRecommendations.registerOutParameter(1, Types.CLOB);
                    getRecommendations.setString(2, query.getSqlId());
                    getRecommendations.execute();

                    Clob clob = getRecommendations.getClob(1);
                    return clob != null ? clob.getSubString(1, (int) clob.length()) : null;
                }
            });

            logger.info("Optimization recommendations for query " + query.getSqlId() + ": " + recommendations);

        } catch (Exception e) {
            logger.error("Unexpected error during tuning for SQL ID: " + query.getSqlId(), e);
            throw new RuntimeException("Unexpected error during tuning: " + e.getMessage(), e);
        }

        query.setOptimizationRecommendations(recommendations);
        query.setStatus("OPTIMIZED");

        return slowQueryRepository.save(query);
    }

    @Transactional
    public StatisticsJob scheduleStatisticsGathering(StatisticsJob job) {
        // Validate job object
        if (job == null) {
            throw new IllegalArgumentException("Statistics job cannot be null");
        }

        // Validate required fields
        if (job.getObjectName() == null || job.getObjectName().trim().isEmpty()) {
            throw new IllegalArgumentException("Object name is required and cannot be empty");
        }

        if (job.getObjectType() == null ||
                (!job.getObjectType().equalsIgnoreCase("TABLE") && !job.getObjectType().equalsIgnoreCase("INDEX"))) {
            throw new IllegalArgumentException("Invalid object type. Must be 'TABLE' or 'INDEX'");
        }

        // Set default schedule expression if not provided
        if (job.getScheduleExpression() == null || job.getScheduleExpression().trim().isEmpty()) {
            // Default to daily at midnight
            job.setScheduleExpression("0 0 * * *");
        }

        // Validate schedule expression
        validateScheduleExpression(job.getScheduleExpression());

        // Set initial run times and metadata
        job.setCreatedAt(LocalDateTime.now());
        job.setStatus("SCHEDULED");
        job.setNextRun(calculateNextRun(job.getScheduleExpression()));

        // Save and return the job
        return statisticsJobRepository.save(job);
    }

    private void validateScheduleExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Schedule expression cannot be empty");
        }

        // More comprehensive cron expression validation
        String[] parts = expression.trim().split("\\s+");
        if (parts.length < 5 || parts.length > 6) {
            throw new IllegalArgumentException("Invalid cron expression format");
        }

        // Optional: Add more detailed cron expression validation
        try {
            // Basic range checks for standard cron parts
            validateCronPart(parts[0], 0, 59);  // Minutes
            validateCronPart(parts[1], 0, 23);  // Hours
            validateCronPart(parts[2], 1, 31);  // Day of month
            validateCronPart(parts[3], 1, 12);  // Month
            validateCronPart(parts[4], 0, 6);   // Day of week
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid cron expression: " + e.getMessage());
        }
    }

    private void validateCronPart(String part, int min, int max) {
        // Skip validation for wildcard
        if ("*".equals(part)) return;

        // Check for range or list
        if (part.contains(",")) {
            for (String subPart : part.split(",")) {
                validateSingleCronPart(subPart, min, max);
            }
        } else {
            validateSingleCronPart(part, min, max);
        }
    }

    private void validateSingleCronPart(String part, int min, int max) {
        // Check for step values
        if (part.contains("/")) {
            String[] stepParts = part.split("/");
            if (stepParts.length != 2) {
                throw new IllegalArgumentException("Invalid step value: " + part);
            }
            part = stepParts[0];
        }

        // Check for ranges
        if (part.contains("-")) {
            String[] rangeParts = part.split("-");
            if (rangeParts.length != 2) {
                throw new IllegalArgumentException("Invalid range: " + part);
            }
            int start = Integer.parseInt(rangeParts[0]);
            int end = Integer.parseInt(rangeParts[1]);

            if (start < min || end > max || start > end) {
                throw new IllegalArgumentException("Range out of bounds: " + part);
            }
        } else {
            // Simple numeric validation
            int value = Integer.parseInt(part);
            if (value < min || value > max) {
                throw new IllegalArgumentException("Value out of bounds: " + part);
            }
        }
    }

    private LocalDateTime calculateNextRun(String expression) {
        // More sophisticated next run calculation
        String[] parts = expression.split("\\s+");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.plusHours(1);  // Default to 1 hour from now

        try {
            // Basic parsing of minutes and hours
            if (!parts[0].equals("*")) {
                int minute = parts[0].equals("*") ? now.getMinute() : Integer.parseInt(parts[0]);
                nextRun = now.withMinute(minute).withSecond(0).withNano(0);
            }

            if (!parts[1].equals("*")) {
                int hour = parts[1].equals("*") ? now.getHour() : Integer.parseInt(parts[1]);
                nextRun = nextRun.withHour(hour);
            }

            // Ensure next run is in the future
            while (nextRun.isBefore(now)) {
                nextRun = nextRun.plusDays(1);
            }
        } catch (Exception e) {
            // Fallback to default
            nextRun = now.plusHours(1);
        }

        return nextRun;
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void executeScheduledStatisticsJobs() {
        try {
            List<StatisticsJob> dueJobs = statisticsJobRepository.findAll().stream()
                    .filter(job -> job.getNextRun().isBefore(LocalDateTime.now()))
                    .toList();

            for (StatisticsJob job : dueJobs) {
                try {
                    gatherStatistics(job);
                    job.setLastRun(LocalDateTime.now());
                    job.setNextRun(calculateNextRun(job.getScheduleExpression()));
                    job.setStatus("SUCCESS");
                } catch (Exception e) {
                    job.setStatus("FAILED: " + e.getMessage());
                    logger.error("Error gathering statistics for job: " + job.getId(), e);
                }
                statisticsJobRepository.save(job);
            }

        } catch (Exception e) {
            logger.error("Error executing scheduled statistics jobs", e);
            throw new RuntimeException("Error executing scheduled statistics jobs", e);
        }
    }

    private void gatherStatistics(StatisticsJob job) {
        String sql = """
            BEGIN
                DBMS_STATS.GATHER_TABLE_STATS(
                    ownname => USER,
                    tabname => ?,
                    estimate_percent => 100,
                    method_opt => 'FOR ALL COLUMNS SIZE AUTO',
                    cascade => TRUE
                );
            END;
        """;

        if ("INDEX".equals(job.getObjectType())) {
            sql = """
                BEGIN
                    DBMS_STATS.GATHER_INDEX_STATS(
                        ownname => USER,
                        indname => ?,
                        estimate_percent => 100
                    );
                END;
            """;
        }

        jdbcTemplate.update(sql, job.getObjectName());
    }

}
