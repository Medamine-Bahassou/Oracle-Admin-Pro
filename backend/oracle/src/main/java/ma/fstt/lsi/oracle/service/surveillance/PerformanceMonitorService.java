package ma.fstt.lsi.oracle.service.surveillance;

import ma.fstt.lsi.oracle.dto.surveillance.ASHReportDTO;
import ma.fstt.lsi.oracle.dto.surveillance.AWRReportDTO;
import ma.fstt.lsi.oracle.dto.surveillance.PerformanceMetricsDTO;
import ma.fstt.lsi.oracle.dto.surveillance.SQLStatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PerformanceMonitorService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public PerformanceMetricsDTO getCurrentMetrics() {
        PerformanceMetricsDTO metrics = new PerformanceMetricsDTO();
        try {
            // CPU Usage
            String cpuQuery = "SELECT NVL(VALUE, 0) FROM V$SYSMETRIC WHERE METRIC_NAME = 'CPU Usage Per Sec' AND ROWNUM = 1";
            Double cpuUsage = jdbcTemplate.queryForObject(cpuQuery, Double.class);
            metrics.setCpuUsage(cpuUsage != null ? cpuUsage : 0.0);

            // Memory Usage - Version corrigée
            String memoryQuery = """
            SELECT NVL(ROUND(
                (SELECT SUM(current_size) FROM V$SGA_DYNAMIC_COMPONENTS) /
                (SELECT value FROM V$PARAMETER WHERE name = 'sga_max_size') * 100,
                2), 0) AS memory_usage_pct
            FROM DUAL
        """;
            Double memoryUsage = jdbcTemplate.queryForObject(memoryQuery, Double.class);
            metrics.setMemoryUsage(memoryUsage != null ? memoryUsage : 0.0);

            // IO Usage
            String ioQuery = "SELECT NVL(VALUE, 0) FROM V$SYSMETRIC WHERE METRIC_NAME = 'Physical Read Total Bytes Per Sec' AND ROWNUM = 1";
            Double ioUsage = jdbcTemplate.queryForObject(ioQuery, Double.class);
            metrics.setIoUsage(ioUsage != null ? ioUsage : 0.0);

            metrics.setTimestamp(System.currentTimeMillis());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching performance metrics: " + e.getMessage(), e);
        }
        return metrics;
    }
    public List<AWRReportDTO> getAWRReport() {
        String query = """
        SELECT 
            snap.snap_id,
            snap.begin_interval_time,
            snap.end_interval_time,
            NVL(stats.sql_id, 'N/A') as sql_id,
            NVL(sqltext.sql_text, 'N/A') as sql_text,
            NVL(stats.executions_delta, 0) as executions,
            NVL(stats.elapsed_time_delta, 0) as elapsed_time,
            NVL(stats.cpu_time_delta, 0) as cpu_time,
            NVL(stats.buffer_gets_delta, 0) as buffer_gets,
            NVL(stats.disk_reads_delta, 0) as disk_reads,
            NVL(stats.rows_processed_delta, 0) as rows_processed,
            NVL(stats.plan_hash_value, 0) as plan_hash_value
        FROM DBA_HIST_SNAPSHOT snap
        LEFT JOIN DBA_HIST_SQLSTAT stats ON snap.snap_id = stats.snap_id
            AND snap.instance_number = stats.instance_number
            AND snap.dbid = stats.dbid
        LEFT JOIN DBA_HIST_SQLTEXT sqltext ON stats.sql_id = sqltext.sql_id
        WHERE snap.snap_id IN (
            SELECT snap_id FROM (
                SELECT snap_id FROM DBA_HIST_SNAPSHOT 
                ORDER BY snap_id DESC
            ) WHERE ROWNUM <= 10
        )
        ORDER BY snap.snap_id DESC, stats.elapsed_time_delta DESC
    """;

        try {
            Map<String, AWRReportDTO> reportMap = new HashMap<>();

            jdbcTemplate.query(query, (rs) -> {
                String snapId = rs.getString("SNAP_ID");
                AWRReportDTO report = reportMap.computeIfAbsent(snapId, k -> {
                    AWRReportDTO newReport = new AWRReportDTO();
                    newReport.setSnapshotId(snapId);
                    try {
                        newReport.setStartTime(rs.getString("BEGIN_INTERVAL_TIME"));
                        newReport.setEndTime(rs.getString("END_INTERVAL_TIME"));
                    } catch (SQLException e) {
                        throw new RuntimeException("Error processing timestamp data", e);
                    }
                    newReport.setTopSQLStats(new ArrayList<>());
                    return newReport;
                });

                SQLStatDTO sqlStat = new SQLStatDTO();
                sqlStat.setSqlId(rs.getString("SQL_ID"));
                sqlStat.setSqlText(rs.getString("SQL_TEXT"));
                sqlStat.setExecutions(rs.getLong("EXECUTIONS"));
                sqlStat.setElapsedTime(rs.getDouble("ELAPSED_TIME"));
                sqlStat.setCpuTime(rs.getDouble("CPU_TIME"));
                sqlStat.setBufferGets(rs.getDouble("BUFFER_GETS"));
                sqlStat.setDiskReads(rs.getDouble("DISK_READS"));
                sqlStat.setRowsProcessed(rs.getDouble("ROWS_PROCESSED"));
                sqlStat.setPlanHashValue(rs.getString("PLAN_HASH_VALUE"));

                report.getTopSQLStats().add(sqlStat);
            });

            return new ArrayList<>(reportMap.values());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching AWR report: " + e.getMessage(), e);
        }
    }


    public List<ASHReportDTO> getASHReport() {
        String query = "SELECT * FROM V$ACTIVE_SESSION_HISTORY WHERE SAMPLE_TIME > SYSDATE - 1/24 ORDER BY SAMPLE_TIME DESC";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            ASHReportDTO report = new ASHReportDTO();
            report.setSessionId(rs.getString("SESSION_ID"));
            report.setSqlId(rs.getString("SQL_ID"));
            report.setEvent(rs.getString("EVENT"));
            report.setWaitClass(rs.getString("WAIT_CLASS"));
            report.setSampleTime(rs.getTimestamp("SAMPLE_TIME"));
            return report;
        });
    }



}
