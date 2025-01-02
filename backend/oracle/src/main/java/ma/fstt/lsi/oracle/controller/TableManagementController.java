package ma.fstt.lsi.oracle.controller;

import ma.fstt.lsi.oracle.dto.TableRequest;
import ma.fstt.lsi.oracle.model.TableInfo;
import ma.fstt.lsi.oracle.service.TableManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableManagementController {

    @Autowired
    private TableManagementService tableManagementService;

    // Create Table
    @PostMapping("/create")
    public ResponseEntity<?> createTable(@RequestBody TableRequest request) {
        try {
            // Assuming you're calling a service to create the table
            tableManagementService.createTable(request.getTableName(), request.getColumns());
            return ResponseEntity.ok("Table created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating table: " + e.getMessage());
        }
     }

    // List All Tables
    @GetMapping("/list")
    public ResponseEntity<List<TableInfo>> listTables() {
        try {
            List<TableInfo> tables = tableManagementService.listTable();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    // Get Table Info
    @GetMapping("/{tableName}")
    public ResponseEntity<?> getTableInfo(@PathVariable String tableName) {
        try {
            TableInfo tableInfo = tableManagementService.getTableInfo(tableName);
            if (tableInfo != null) {
                return ResponseEntity.ok(tableInfo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching table info: " + e.getMessage());
        }
    }

    // Edit Table
    @PutMapping("/{tableName}")
    public ResponseEntity<?> editTable(@PathVariable String tableName, @RequestBody List<String> newColumns) {
        try {
            tableManagementService.editTable(tableName, newColumns);
            return ResponseEntity.ok("Table updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error editing table: " + e.getMessage());
        }
    }

    // Delete Table
    @DeleteMapping("/{tableName}")
    public ResponseEntity<?> deleteTable(@PathVariable String tableName) {
        try {
            tableManagementService.deleteTable(tableName);
            return ResponseEntity.ok("Table deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting table: " + e.getMessage());
        }
    }

    // List Columns of a Table
    @GetMapping("/columns/{tableName}")
    public ResponseEntity<?> listColumns(@PathVariable String tableName) {
        try {
            List<String> columns = tableManagementService.listColumns(tableName);
            return ResponseEntity.ok(columns);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching columns: " + e.getMessage());
        }
    }
}
