import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AshService {
  private apiUrl = 'http://localhost:8080/api/performance'; // Update with correct URL if needed

  constructor(private http: HttpClient) {
  }

  getASHReport(): Observable<any> {
    return this.http.get(`${this.apiUrl}/ash`).pipe(
      catchError((err) => {
        console.error('Error fetching ash report:', err);
        return throwError(() => new Error('Failed to fetch ash report.'));
      })
    );
  }
  getASHChartData(eventFilter?: string): Observable<any> {
    let params = new HttpParams();
    if(eventFilter) {
      params = params.set('eventFilter', eventFilter)
    }
    return this.http.get(`${this.apiUrl}/ash-chart`, { params }).pipe(
      catchError((err) => {
        console.error('Error fetching ash chart data:', err);
        return throwError(() => new Error('Failed to fetch ash chart data.'));
      })
    );
  }
}
