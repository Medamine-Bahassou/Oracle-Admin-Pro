import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {AdminComponent} from './components/admin/admin.component';
import {LandingComponent} from './components/landing/landing.component';

const routes: Routes = [
  { path: '', redirectTo: '/landing', pathMatch: 'full' }, // Default route
  { path: 'landing', component: LandingComponent }, // Overview page

  // Parent route for dashboard with child routes
  {
    path: 'admin',
    component: AdminComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }, // Default child route
      { path: 'dashboard', component: DashboardComponent }, // Overview page
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
