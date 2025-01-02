import { Component, OnInit } from '@angular/core';
import { TableManagementService } from '../../services/TableManagementService/table-management-service.service';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-table-management-component',
  templateUrl: './table-management-component.component.html',
  imports: [
    NgClass,
    NgIf,
    NgForOf,
    FormsModule
  ],
  styleUrls: ['./table-management-component.component.scss']
})
export class TableManagementComponent implements OnInit {
  tables: any[] = [];
  newTableName: string = '';
  newTableColumns: string = '';
  selectedTable: any = null;
  message: string = '';
  messageType: 'success' | 'error' = 'success';
  displayedColumns: string[] = ['name', 'columns', 'actions'];

  constructor(private tableManagementService: TableManagementService) { }

  ngOnInit() {
    this.loadTables();
  }

  loadTables() {
    this.tableManagementService.listTables().subscribe(
      (data) => {
console.log(data);
        this.tables = data;
      },
      (error) => {
        this.showMessage(`Error loading tables: ${error.message}`, 'error');
      }
    );
  }

  createTable() {
    if (!this.newTableName || !this.newTableColumns) {
      this.showMessage('Please fill in all fields', 'error');
      return;
    }

    const columns = this.newTableColumns.split(',').map(col => col.trim());

    const tableRequest = {
      tableName: this.newTableName,
      columns: columns
    };
  console.log(tableRequest);
    this.tableManagementService.createTable(tableRequest).subscribe(
      () => {
        this.showMessage('Table created successfully', 'success');
        this.loadTables();
        this.newTableName = '';
        this.newTableColumns = '';
      },
      (error) => {
        this.showMessage(`Error creating table: ${error.message}`, 'error');
      }
    );
  }

  selectTable(table: any) {
    this.selectedTable = this.selectedTable?.tableName === table.tableName ? null : table;
  }

  editTable() {
    if (this.selectedTable) {
      const newColumns = this.selectedTable.columns;

      this.tableManagementService.editTable(this.selectedTable.tableName, newColumns).subscribe(
        () => {
          this.showMessage('Table updated successfully', 'success');
          this.loadTables();
        },
        (error) => {
          this.showMessage(`Error updating table: ${error.message}`, 'error');
        }
      );
    }
  }

  deleteTable(tableName: string) {
    if (confirm('Are you sure you want to delete this table?')) {
      this.tableManagementService.deleteTable(tableName).subscribe(
        () => {
          this.showMessage('Table deleted successfully', 'success');
          this.loadTables();
          this.selectedTable = null;
        },
        (error) => {
          this.showMessage(`Error deleting table: ${error.message}`, 'error');
        }
      );
    }
  }

  showMessage(msg: string, type: 'success' | 'error') {
    this.message = msg;
    this.messageType = type;
    setTimeout(() => {
      this.message = '';
    }, 5000);
  }
}
