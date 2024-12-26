package ma.fstt.lsi.oracle.controller;

import ma.fstt.lsi.oracle.dto.RoleRequest;
import ma.fstt.lsi.oracle.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignRoleToUser(@RequestBody RoleRequest request) {
        try {
            roleService.assignRoleToUser(request.getUsername(), request.getRole());
            return ResponseEntity.ok("Role " + request.getRole() + " assigned to user " + request.getUsername() + " successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoleWithPrivileges(@RequestBody RoleRequest request) {
        try {
            roleService.createRoleWithPrivileges(request.getRoleName(), request.getPrivileges());
            return ResponseEntity.ok("Role " + request.getRoleName() + " created with privileges successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revokeRoleFromUser(@RequestParam String username, @RequestParam String role) {
        try {
            roleService.revokeRoleFromUser(username, role);
            return ResponseEntity.ok("Role " + role + " revoked from user " + username + " successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/drop")
    public ResponseEntity<?> dropRole(@RequestParam String roleName) {
        try {
            roleService.dropRole(roleName);
            return ResponseEntity.ok("Role " + roleName + " dropped successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        try {
            return ResponseEntity.ok(roleService.getAllRoles());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserRoles(@PathVariable String username) {
        try {
            return ResponseEntity.ok(roleService.getUserRoles(username));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{roleName}/privileges")
    public ResponseEntity<?> getRolePrivileges(@PathVariable String roleName) {
        try {
            return ResponseEntity.ok(roleService.getRolePrivileges(roleName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}