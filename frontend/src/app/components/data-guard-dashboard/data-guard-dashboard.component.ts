import {Component, OnInit} from '@angular/core';
import {DataGuardService} from '../../services/DataGuardService/data-guard-service.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faSync, faExchangeAlt, faExclamationTriangle, faUndoAlt } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-data-guard-dashboard',
  standalone: false,

  templateUrl: './data-guard-dashboard.component.html',
  styleUrl: './data-guard-dashboard.component.scss'
})
export class DataGuardDashboardComponent implements OnInit {
  configs: any[] = [];
  availabilityReport: string = '';
  faSync = faSync;
  faExchangeAlt = faExchangeAlt;
  faExclamationTriangle = faExclamationTriangle;
  faUndoAlt = faUndoAlt;
  constructor(private dataGuardService: DataGuardService) { }

  ngOnInit(): void {
    this.loadConfigs();
    this.loadAvailabilityReport();
  }

  loadConfigs(): void {
    this.dataGuardService.getAllConfigs().subscribe(
      (data) => this.configs = data,
      (error) => console.error('Error loading configs:', error)
    );
  }

  loadAvailabilityReport(): void {
    this.dataGuardService.getAvailabilityReport().subscribe(
      (data) => this.availabilityReport = data,
      (error) => console.error('Error loading availability report:', error)
    );
  }

  updateStatus(): void {
    this.dataGuardService.updateStatus().subscribe(
      () => {
        this.loadConfigs();
        this.loadAvailabilityReport();
      },
      (error) => console.error('Error updating status:', error)
    );
  }

  simulateSwitchover(): void {
    this.dataGuardService.simulateSwitchover().subscribe(
      () => this.updateStatus(),
      (error) => console.error('Error simulating switchover:', error)
    );
  }

  simulateFailover(): void {
    this.dataGuardService.simulateFailover().subscribe(
      () => this.updateStatus(),
      (error) => console.error('Error simulating failover:', error)
    );
  }

  simulateFailback(): void {
    this.dataGuardService.simulateFailback().subscribe(
      () => this.updateStatus(),
      (error) => console.error('Error simulating failback:', error)
    );
  }


}

