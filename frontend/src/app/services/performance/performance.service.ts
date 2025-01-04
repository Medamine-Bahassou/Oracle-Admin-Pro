import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {
  private apiUrl = 'http://localhost:8080/api/performance'; // Update with correct URL if needed

  constructor(private http: HttpClient) {}

  getCurrentMetrics(): Observable<any> {
    return this.http.get(`${this.apiUrl}/metrics`).pipe(
      catchError((err) => {
        console.error('Error fetching metrics:', err);
        return throwError(() => new Error('Failed to fetch performance metrics.'));
      })
    );
  }

}
