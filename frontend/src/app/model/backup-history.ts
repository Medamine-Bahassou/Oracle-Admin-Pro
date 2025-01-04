export class BackupHistory {
  id!: number;
  type!: string; // COMPLETE or INCREMENTAL
  status!: string; // SUCCESS or FAILURE
  backupDate!: string; // ISO string for date
  backupLocation!: string;
  restoreDate?: string; // Optional
}
