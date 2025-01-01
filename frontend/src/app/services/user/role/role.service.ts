import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
  }
)
export class RoleService {
  private apiUrl = 'http://localhost:8080/api/roles'; // Update with your backend URL

  constructor(private http: HttpClient) { }

  assignRoleToUser(username: string, role: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/assign`, { username, role }, { observe: 'response', responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }

  createRole(roleName: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, { roleName }, { observe: 'response', responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }

  grantPrivilegesToRole(roleName: string, privileges: string[]): Observable<any> {
    return this.http.post(`${this.apiUrl}/grantPrivileges`, { roleName, privileges }, { observe: 'response', responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }
  revokeRoleFromUser(username: string, role: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/revoke?username=${username}&role=${role}`)
      .pipe(
        catchError(this.handleError)
      );
  }
  dropRole(roleName: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/drop?roleName=${roleName}`, { observe: 'response', responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }
  getAllRoles(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl)
      .pipe(
        catchError(this.handleError)
      );
  }

  getUserRoles(username: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${username}`)
      .pipe(
        catchError(this.handleError)
      );
  }
  getRolePrivileges(roleName: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${roleName}/privileges`)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: any) {
    if (error instanceof HttpErrorResponse && error.status === 200) {
      console.warn('HTTP 200 OK but response treated as error:', error);
      return throwError(() => new Error('The role is created/deleted/updated/assigned successfully, but a warning has occurred.'));
    }
    console.error('An error occurred:', error);
    return throwError(() => new Error('Something went wrong. Please try again later.'));
  }
}
