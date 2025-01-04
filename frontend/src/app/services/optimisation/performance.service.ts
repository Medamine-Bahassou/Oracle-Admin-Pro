import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {
  private apiUrl = 'http://localhost:8080/api/performance';

  constructor(private http: HttpClient) { }

  getSlowQueries(): Observable<SlowQuery[]> {
    return this.http.get<SlowQuery[]>(`${this.apiUrl}/slow-queries`);
  }

  optimizeQuery(queryId: number): Observable<SlowQuery> {
    return this.http.post<SlowQuery>(`${this.apiUrl}/optimize-query/${queryId}`, null);
  }

  scheduleStatistics(job: StatisticsJob): Observable<StatisticsJob | ErrorResponse> {
    return this.http.post<StatisticsJob | ErrorResponse>(`${this.apiUrl}/schedule-statistics`, job);
  }
}
