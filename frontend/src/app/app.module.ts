import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AdminComponent } from './components/admin/admin.component';
import { LandingComponent } from './components/landing/landing.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import {BaseChartDirective} from 'ng2-charts';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import {HttpClientModule} from '@angular/common/http';
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
    AppRoutingModule,
    BaseChartDirective,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
