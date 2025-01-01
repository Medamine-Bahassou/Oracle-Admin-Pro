import { Component, OnInit } from '@angular/core';


import { DatePipe, NgClass, NgForOf, NgIf } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faDatabase, faSync, faHistory, faRedo } from '@fortawesome/free-solid-svg-icons';
import Swal from 'sweetalert2';
import {BackupService} from '../../services/servie-backup/backup.service';
import {catchError, finalize, of, timeout} from 'rxjs';

@Component({
  selector: 'app-backup',
  templateUrl: './backup.component.html',
  imports: [NgForOf, NgClass, NgIf, DatePipe, FontAwesomeModule],
  styleUrls: ['./backup.component.scss']
})
export class BackupComponent implements OnInit {
  backupHistory: BackupHistory[] = [];
  isLoading = true;
  isOperationLoading = false;
  error: string | null = null;
  result: string = '';

  // Icons
  faDatabase = faDatabase;
  faSync = faSync;
  faHistory = faHistory;
  faRedo = faRedo;

  constructor(private backupService: BackupService) {}

  ngOnInit() {
    this.loadBackupHistory();
  }

  loadBackupHistory() {
    this.isLoading = true;
    this.error = null;
    this.backupService.getBackupHistory().subscribe({
      next: (history) => {
        this.backupHistory = history.filter(backup => backup.type !== null);
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load backup history. Please try again.';
        this.isLoading = false;
        console.error('Error loading backup history:', err);
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
    this.showLoadingAlert();
    this.backupService.performIncrementalBackup(level).subscribe({
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
        this.showLoadingAlert();
        this.backupService.performRestore().subscribe({
          next: (response) => {
            this.result = response;
            this.handleSuccess('Restore completed successfully');
            this.loadBackupHistory();
          },
          error: (error) => {
            this.handleError(error);
          }
        });
      }
    });
  }
}
