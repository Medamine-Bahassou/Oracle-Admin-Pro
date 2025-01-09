import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataGuardService {
  private apiUrl = 'http://localhost:8080/api/dataguard';

  constructor(private http: HttpClient) { }

  getAllConfigs(): Observable<any> {
    return this.http.get(`${this.apiUrl}/configs`);
  }

  updateStatus(): Observable<any> {
    return this.http.post(`${this.apiUrl}/update-status`, {});
  }

  simulateSwitchover(): Observable<any> {
    return this.http.post(`${this.apiUrl}/simulate/switchover`, {});
  }

  simulateFailover(): Observable<any> {
    return this.http.post(`${this.apiUrl}/simulate/failover`, {});
  }

  simulateFailback(): Observable<any> {
    return this.http.post(`${this.apiUrl}/simulate/failback`, {});
  }

  getAvailabilityReport(): Observable<any> {
    return this.http.get(`${this.apiUrl}/report`, { responseType: 'text' });
  }
}
