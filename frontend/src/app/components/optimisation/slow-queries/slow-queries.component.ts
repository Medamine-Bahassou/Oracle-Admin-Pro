import { Component, OnInit } from '@angular/core';
import {PerformanceService} from '../../../services/optimisation/performance.service';

interface SlowQuery {
  id: number;
  sqlId: string;
  sqlText: string;
  elapsedTime: number;
  cpuTime: number;
  executions: number;
  captureTime: string;
  status: string;
  optimizationRecommendations: string;
}

@Component({
  selector: 'app-slow-queries',
  standalone: false,
  templateUrl: './slow-queries.component.html',
  styleUrl: './slow-queries.component.scss'
})
export class SlowQueriesComponent implements OnInit {
  slowQueries: SlowQuery[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private performanceService: PerformanceService) { }

  ngOnInit(): void {
    this.loadSlowQueries();
  }

  loadSlowQueries(): void {
    this.loading = true;
    this.error = '';
    this.performanceService.getSlowQueries().subscribe({
      next: (queries) => {
        this.slowQueries = queries;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load slow queries.';
        this.loading = false;
      }
    });
  }

  optimizeQuery(queryId: number): void {
    this.loading = true;
    this.error = '';
    this.performanceService.optimizeQuery(queryId).subscribe({
      next: (optimizedQuery) => {
        const index = this.slowQueries.findIndex(query => query.id === optimizedQuery.id);
        if(index !== -1) {
          this.slowQueries[index] = optimizedQuery;
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to optimize the query';
        this.loading = false;
      }
    });
  }
}
