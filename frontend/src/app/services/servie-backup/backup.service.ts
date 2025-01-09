import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {BackupHistory} from '../../model/backup-history';


@Injectable({
  providedIn: 'root'
})

export class BackupService {
  private baseUrl = 'http://localhost:8080/api/backups'; // Replace with your Spring backend URL

  constructor(private http: HttpClient) {}

  performFullBackup(): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/full`, {});
  }

  performIncrementalBackup(level: number): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/incremental?level=${level}`, {});
  }

  getBackupHistory(): Observable<BackupHistory[]> {
    return this.http.get<BackupHistory[]>(`${this.baseUrl}/history`);
  }

  getBackupHistoryByDateRange(startDate: string, endDate: string): Observable<BackupHistory[]> {
    return this.http.get<BackupHistory[]>(
      `${this.baseUrl}/history/date?startDate=${startDate}&endDate=${endDate}`
    );
  }

  performRestore(): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/restore`, {});
  }

  createSchedule(scheduleData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/schedule`, scheduleData);
  }

  getCurrentSchedule(): Observable<any> {
    return this.http.get(`${this.baseUrl}/schedule/current`);
  }

  deleteSchedule(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/schedule/${id}`);
  }
}
