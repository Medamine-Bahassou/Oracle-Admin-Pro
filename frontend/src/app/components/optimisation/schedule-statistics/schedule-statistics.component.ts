import { Component } from '@angular/core';
import { PerformanceService } from '../../../services/optimisation/performance.service';

interface StatisticsJob {
  objectName: string;
  objectType: 'TABLE' | 'INDEX';
  scheduleExpression: string;
  lastRun: string | null;
  nextRun: string | null;
  status: string;
  createdAt: string;
}
interface ErrorResponse {
  error: string;
}

@Component({
  selector: 'app-schedule-statistics',
  standalone: false,
  templateUrl: './schedule-statistics.component.html',
  styleUrl: './schedule-statistics.component.scss'
})
export class ScheduleStatisticsComponent {
  job: StatisticsJob = {
    objectName: '',
    objectType: 'TABLE',
    scheduleExpression: '0 0 * * *', // Default daily at midnight
    lastRun: null,
    nextRun: null,
    status: '',
    createdAt:''
  };
  successMessage: string | null = null;
  errorMessage: string | null = null;
  loading: boolean = false;
  scheduledJob: StatisticsJob | null = null;

  constructor(private performanceService: PerformanceService) { }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;
    this.scheduledJob = null; // Reset on new submission

    const hardcodedJob: StatisticsJob = {
      objectName: "dddd",
      objectType: "TABLE",
      scheduleExpression: "0 2 * * *",
      lastRun: null,
      nextRun: null,
      status: '',
      createdAt:''
    };

    this.performanceService.scheduleStatistics(hardcodedJob).subscribe({
      next: (response) => {
        this.loading = false;
        if ('error' in response) {
          this.errorMessage = (response as ErrorResponse).error;
        } else {
          this.successMessage = 'Statistics job scheduled successfully!';
          this.scheduledJob = response as StatisticsJob;
          this.job =  { // Only reset the properties you want to change
            objectName: '',
            objectType: 'TABLE',
            scheduleExpression: '0 0 * * *',
            lastRun: null,
            nextRun: null,
            status: '',
            createdAt:''
          };
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =  'Failed to schedule statistics job.';
      }
    });
  }
}
