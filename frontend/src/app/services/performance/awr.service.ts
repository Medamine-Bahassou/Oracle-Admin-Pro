// awr.service.ts
import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AwrService {

  private apiUrl = 'http://localhost:8080/api/performance'; // Update with correct URL if needed


  constructor(private http: HttpClient) {}

  getAWRReport(): Observable<any> {
    return this.http.get(`${this.apiUrl}/awr`).pipe(
      catchError((err) => {
        console.error('Error fetching awr report:', err);
        return throwError(() => new Error('Failed to fetch awr report.'));
      })
    );
  }

}
