import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {AdminComponent} from './components/admin/admin.component';
import {LandingComponent} from './components/landing/landing.component';
import {BackupComponent} from './components/backup/backup.component';

import {TableManagementComponent} from './components/table-management-component/table-management-component.component';
import {SecurityComponent} from './components/security-dashboard/security-dashboard.component';
import {UsersComponent} from './components/users/users.component';
import {SlowQueriesComponent} from './components/optimisation/slow-queries/slow-queries.component';
import {ScheduleStatisticsComponent} from './components/optimisation/schedule-statistics/schedule-statistics.component';

const routes: Routes = [
  { path: '', redirectTo: '/landing', pathMatch: 'full' }, // Default route
  { path: 'landing', component: LandingComponent }, // Overview page

  // Parent route for dashboard with child routes
  {
    path: 'admin',
    component: AdminComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }, // Default child route
      { path: 'dashboard', component: DashboardComponent },
      { path: 'backup', component: BackupComponent },
      { path: 'security', component: SecurityComponent },
      { path: 'table', component: TableManagementComponent },// Overview page
      { path: 'backup', component: BackupComponent },// Overview page
      { path: 'dashboard', component: DashboardComponent }, // Dashboard page
      { path: 'users', component: UsersComponent },
      { path: 'slow', component: SlowQueriesComponent },
      { path: 'schedule', component: ScheduleStatisticsComponent },
    ]
  },

  //{ path: 'settings', component: SettingsComponent }, // Settings page
  //{ path: 'user-management', component: UserManagementComponent }, // User management page
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
