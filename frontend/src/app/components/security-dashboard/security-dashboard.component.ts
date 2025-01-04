import { Component, OnInit } from '@angular/core';
import { SecurityService } from '../../services/service-security/security.service';
import { NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-security-dashboard',
  templateUrl: './security-dashboard.component.html',
  imports: [FormsModule, NgForOf],
  standalone: true,
  styleUrls: ['./security-dashboard.component.scss']
})
export class SecurityComponent implements OnInit {
  tables: string[] = [];
  columns: string[] = [];
  selectedTable: string = '';
  selectedColumn: string = '';
  policyFunction: string = '';
  message: string = '';

  constructor(private securityService: SecurityService) {}

  ngOnInit() {
    this.loadTables();
  }

  loadTables() {
    this.securityService.getTables().subscribe(
      (data) => {
        this.tables = data;
      },
      (error) => {
        this.showError('Error loading tables', error.message);
      }
    );
  }

  onTableSelect() {
    if (this.selectedTable) {
      this.securityService.getColumns(this.selectedTable).subscribe(
        (data) => {
          this.columns = data;
          this.selectedColumn = '';
        },
        (error) => {
          this.showError('Error loading columns', error.message);
        }
      );
    } else {
      this.columns = [];
      this.selectedColumn = '';
    }
  }

  enableTDE() {
    if (this.selectedTable && this.selectedColumn) {
      this.securityService.enableTDE(this.selectedTable, this.selectedColumn).subscribe(
        () => {
          this.showSuccess('TDE enabled successfully');
        },
        (error) => {
          this.showError('Error enabling TDE', error.message);
        }
      );
    } else {
      this.showWarning('Please select both table and column');
    }
  }

  enableAudit() {
    if (this.selectedTable) {
      this.securityService.enableAudit(this.selectedTable).subscribe(
        () => {
          this.showSuccess('Audit enabled successfully');
        },
        (error) => {
          this.showError('Error enabling audit', error.message);
        }
      );
    } else {
      this.showWarning('Please select a table');
    }
  }

  configureVPD() {
    if (this.selectedTable && this.policyFunction) {
      this.securityService.configureVPD(this.selectedTable, this.policyFunction).subscribe(
        () => {
          this.showSuccess('VPD configured successfully');
        },
        (error) => {
          this.showError('Error configuring VPD', error.message);
        }
      );
    } else {
      this.showWarning('Please select a table and enter a policy function');
    }
  }

  private showSuccess(message: string) {
    Swal.fire({
      title: 'Success!',
      text: message,
      icon: 'success',
      confirmButtonColor: '#3085d6'
    });
  }

  private showError(title: string, message: string) {
    Swal.fire({
      title: title,
      text: message,
      icon: 'error',
      confirmButtonColor: '#d33'
    });
  }

  private showWarning(message: string) {
    Swal.fire({
      title: 'Warning',
      text: message,
      icon: 'warning',
      confirmButtonColor: '#f8bb86'
    });
  }
}
