import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SecurityService {
  private apiUrl = 'http://localhost:8080/api/security'; // Adjust this URL as needed

  constructor(private http: HttpClient) { }

  enableTDE(tableName: string, columnName: string): Observable<any> {
    const params = new HttpParams()
      .set('tableName', tableName)
      .set('columnName', columnName);
    return this.http.post(`${this.apiUrl}/tde/enable`, null, { params });
  }

  enableAudit(tableName: string): Observable<any> {
    const params = new HttpParams().set('tableName', tableName);
    return this.http.post(`${this.apiUrl}/audit/enable`, null, { params });
  }

  configureVPD(tableName: string, policyFunction: string): Observable<any> {
    const params = new HttpParams()
      .set('tableName', tableName)
      .set('policyFunction', policyFunction);
    return this.http.post(`${this.apiUrl}/vpd/configure`, null, { params });
  }
  getTables(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/tables`);
  }

  getColumns(tableName: string): Observable<string[]> {
    const params = new HttpParams().set('tableName', tableName);
    return this.http.get<string[]>(`${this.apiUrl}/columns`, { params });
  }
}
