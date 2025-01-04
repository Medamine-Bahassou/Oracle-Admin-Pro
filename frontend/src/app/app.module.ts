import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AdminComponent } from './components/admin/admin.component';
import { LandingComponent } from './components/landing/landing.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  TableManagementComponent,

} from './components/table-management-component/table-management-component.component';
import {SecurityComponent} from './components/security-dashboard/security-dashboard.component';

import {BackupComponent} from './components/backup/backup.component';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {BaseChartDirective} from 'ng2-charts';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { UsersComponent } from './components/users/users.component';
import {FormsModule} from '@angular/forms';
import { SlowQueriesComponent } from './components/optimisation/slow-queries/slow-queries.component';
import { ScheduleStatisticsComponent } from './components/optimisation/schedule-statistics/schedule-statistics.component';

@NgModule({
  declarations: [
    AppComponent,
    AdminComponent,
    LandingComponent,
    NavbarComponent,
    DashboardComponent,
    UsersComponent,
    SlowQueriesComponent,
    ScheduleStatisticsComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    BackupComponent,
    HttpClientModule,
    FontAwesomeModule,
    FormsModule,
    SecurityComponent,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatInputModule,
    TableManagementComponent,


    BaseChartDirective,
    FormsModule
  ],
  providers: [], // Add SecurityService here
  bootstrap: [AppComponent]
})
export class AppModule { }
