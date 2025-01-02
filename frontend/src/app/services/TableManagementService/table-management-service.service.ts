import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TableManagementService {
  private apiUrl = 'http://localhost:8080/api/tables';

  constructor(private http: HttpClient) {
  }

  // Create a table with name and columns
  createTable(tableRequest: { tableName: string, columns: string[] }): Observable<any> {
    // Sending the tableRequest object as the request body
    return this.http.post(`${this.apiUrl}/create`, tableRequest);
  }


  // List all tables
  listTables(): Observable<any> {
    return this.http.get(`${this.apiUrl}/list`);
  }

  // Get information of a specific table
  getTableInfo(tableName: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${tableName}`);
  }

  // Edit the columns of a table
  editTable(tableName: string, newColumns: string[]): Observable<any> {
    return this.http.put(`${this.apiUrl}/${tableName}`, newColumns);
  }

  // Delete a table
  deleteTable(tableName: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${tableName}`);
  }
}
