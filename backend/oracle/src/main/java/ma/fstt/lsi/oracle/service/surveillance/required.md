# Required !

Grant these privileges to your database user

```
GRANT SELECT ON V$SYSMETRIC TO c##user;
GRANT SELECT ON V$SGA TO c##user;
GRANT SELECT ON V$PARAMETER TO c##user;
GRANT SELECT ON DBA_HIST_SNAPSHOT TO c##user;
GRANT SELECT ON DBA_HIST_SQLSTAT TO c##user;
GRANT SELECT ON DBA_HIST_SQLTEXT TO c##user;

```