import { Component, OnInit } from '@angular/core';


import { DatePipe, NgClass, NgForOf } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import {
  faDatabase,
  faSync,
  faHistory,
  faRedo,
  faCalendar,
  faSearch,
  faChevronLeft,
  faChevronRight
} from '@fortawesome/free-solid-svg-icons';import Swal from 'sweetalert2';
import {BackupService} from '../../services/servie-backup/backup.service';

import {catchError, finalize, of, timeout} from 'rxjs';
import {BackupHistory} from '../../model/backup-history';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-backup',
  templateUrl: './backup.component.html',
  imports: [NgForOf, NgClass, DatePipe, FontAwesomeModule, FormsModule],
  standalone: true,
  styleUrls: ['./backup.component.scss']
})
export class BackupComponent implements OnInit {
  backupHistory: BackupHistory[] = [];
  isLoading = true;
  isOperationLoading = false;
  error: string | null = null;
  result: null | string = '';
  backupStats = {
    fullBackups: 0,
    incrementalBackups: 0,
    restores: 0,
    failedBackups: 0
  };
  dateRange = {
    startDate: '',
    endDate: ''
  };

  // New properties for table features
  searchTerm: string = '';
  pageSize: number = 5;
  currentPage: number = 1;
  totalItems: number = 0;
  filteredBackups: BackupHistory[] = [];
  Math = Math; // For using Math in template

  faDatabase = faDatabase;
  faSync = faSync;
  faHistory = faHistory;
  faRedo = faRedo;
  faSearch = faSearch;
  faChevronLeft = faChevronLeft;
  faChevronRight = faChevronRight;
  constructor(private backupService: BackupService) {}

  ngOnInit() {
    this.loadBackupHistory();
  }

  calculateStats(history: BackupHistory[]) {
    this.backupStats = history.reduce((stats, backup) => ({
      fullBackups: stats.fullBackups + (backup.type === 'FULL' ? 1 : 0),
      incrementalBackups: stats.incrementalBackups + (backup.type === 'INCREMENTAL' ? 1 : 0),
      restores: stats.restores + (backup.type === 'RESTORE' ? 1 : 0),
      failedBackups: stats.failedBackups + (backup.status === 'Failed' ? 1 : 0)
    }), { fullBackups: 0, incrementalBackups: 0, restores: 0, failedBackups: 0 });
  }

// Modify your loadBackupHistory method

  onSearch(term: string) {
    this.searchTerm = term;
    this.currentPage = 1;
    this.applyFilters();
  }

  onPageSizeChange(size: number) {
    this.pageSize = size;
    this.currentPage = 1;
    this.applyFilters();
  }

  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.applyFilters();
    }
  }

  nextPage() {
    if (this.currentPage * this.pageSize < this.totalItems) {
      this.currentPage++;
      this.applyFilters();
    }
  }

  applyFilters() {
    // Filter by search term
    let filtered = this.backupHistory;
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(backup =>
        backup.id ||
        backup.type.toLowerCase().includes(term) ||
        backup.status.toLowerCase().includes(term) ||
        backup.backupLocation.toLowerCase().includes(term)
      );
    }

    // Update total count
    this.totalItems = filtered.length;

    // Apply pagination
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    this.filteredBackups = filtered.slice(start, end);
  }

  loadBackupHistory() {
    this.isLoading = true;
    this.error = null;

    const endpoint = this.dateRange.startDate && this.dateRange.endDate
      ? this.backupService.getBackupHistoryByDateRange(this.dateRange.startDate, this.dateRange.endDate)
      : this.backupService.getBackupHistory();

    endpoint.subscribe({
      next: (history) => {
        this.backupHistory = history.filter(backup => backup.type !== null);
        this.calculateStats(this.backupHistory);
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load backup history';
        this.isLoading = false;
        console.error('Error:', err);
      }
    });
  }
  private showLoadingAlert() {
    return Swal.fire({
      title: 'Backup in Progress',
      html: `
        <div class="text-left">
          <p>Performing RMAN backup operations:</p>
          <ul class="mt-2">
            <li>• Archiving current log</li>
            <li>• Backing up database files</li>
            <li>• Creating control file backup</li>
          </ul>
          <p class="mt-2">This process may take several minutes...</p>
        </div>
      `,
      allowOutsideClick: false,
      allowEscapeKey: false,
      showConfirmButton: false,
      didOpen: () => {
        Swal.showLoading();
      }
    });
  }

  private handleSuccess(response: any) {
    // Check if response contains RMAN completion message
    const isRmanSuccess = typeof response === 'string' &&
      (response.includes('Recovery Manager complete') ||
        response.includes('Backup Successful'));

    if (isRmanSuccess) {
      Swal.fire({
        icon: 'success',
        title: 'Backup Successful',
        html: `
          <div class="text-left">
            <p>RMAN backup completed successfully!</p>
            <p class="mt-2">All database files and archive logs have been backed up.</p>
          </div>
        `,
        timer: 5000,
        timerProgressBar: true,
        showConfirmButton: true
      });
    } else {
      Swal.fire({
        icon: 'success',
        title: 'Operation Complete',
        text: 'The operation completed successfully',
        timer: 3000,
        showConfirmButton: true
      });
    }
  }

  private handleError(error: any) {
    Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'An error occurred while processing your request.'
    });
  }


  performFullBackup() {
    this.isOperationLoading = true;
    this.showLoadingAlert();

    this.backupService.performFullBackup()
      .pipe(
        timeout(600000), // 10 minute timeout
        catchError(error => {
          if (error?.error?.text?.includes('Recovery Manager complete') ||
            error?.error?.text?.includes('Backup Successful')) {
            return of(error.error.text);
          }
          throw error;
        }),
        finalize(() => {
          this.isOperationLoading = false;
        })
      )
      .subscribe({
        next: (response) => {
          this.result = response;
          this.handleSuccess(response);
          this.loadBackupHistory();
        },
        error: (error) => {
          this.handleError(error);
        }
      });
  }


  pollBackupStatus() {
    const interval = setInterval(() => {
      this.backupService.getBackupHistory().subscribe({
        next: (history) => {
          const lastBackup = history[0]; // Assuming the last backup is the most recent
          if (lastBackup.status === 'Completed') {
            clearInterval(interval); // Stop polling
            Swal.close(); // Close the loading alert
            this.handleSuccess('Full backup completed successfully');
            this.loadBackupHistory(); // Reload history to show updated status
          } else if (lastBackup.status === 'Failed') {
            clearInterval(interval); // Stop polling
            Swal.close(); // Close the loading alert
            this.handleError('Backup failed. Please try again.');
          }
        },
        error: (err) => {
          console.error('Error checking backup status:', err);
          clearInterval(interval); // Stop polling if there's an error
          Swal.close(); // Close the loading alert
        }
      });
    }, 5000); // Poll every 5 seconds
  }
  performIncrementalBackup(level: number) {
    this.isOperationLoading = true;
    this.showLoadingAlert();

    this.backupService.performIncrementalBackup(level)
      .pipe(
        timeout(600000), // 10 minute timeout
        catchError(error => {
          if (error?.error?.text?.includes('Recovery Manager complete') ||
            error?.error?.text?.includes('Backup Successful')) {
            return of(error.error.text);
          }
          throw error;
        }),
        finalize(() => {
          this.isOperationLoading = false;
        })
      )
      .subscribe({
        next: (response) => {
          this.result = response;
          this.handleSuccess(`Incremental backup level ${level} completed successfully`);
          this.loadBackupHistory();
        },
        error: (error) => {
          this.handleError(error);
        }
      });
  }

  performRestore() {
    Swal.fire({
      title: 'Are you sure?',
      text: 'This will restore your data to the last backup point.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, restore it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isOperationLoading = true;
        this.showLoadingAlert();

        this.backupService.performRestore()
          .pipe(
            timeout(1800000), // 30-minute timeout (adjust as needed)
            catchError(error => {
              // Check if the error contains RMAN success message
              if (error?.error?.text?.includes('Restore and database open successful') ||
                error?.error?.text?.includes('Recovery Manager')) {
                return of(error.error.text); // Treat this as success
              }
              throw error; // Rethrow other errors
            }),
            finalize(() => {
              this.isOperationLoading = false;
            })
          )
          .subscribe({
            next: (response) => {
              this.result = response;
              this.handleSuccess('Restore completed successfully');
              this.loadBackupHistory();
            },
            error: (error) => {
              this.handleError('Restore operation failed. Please try again.');
              console.error('Restore error:', error);
            }
          });
      }
    });
  }

}
