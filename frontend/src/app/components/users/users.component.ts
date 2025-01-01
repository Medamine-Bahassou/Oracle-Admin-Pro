import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { UserService } from '../../services/user/user.service';
import { Subject, takeUntil, forkJoin } from 'rxjs';
import Swal from 'sweetalert2';
import { RoleService } from "../../services/user/role/role.service";

interface User {
  id?: number
  username: string
  password?: string
  defaultTablespace: string
  temporaryTablespace: string
  profile: string
  quota: string
  accountLocked: boolean
  selectedRole?: string
  userRoles?: string[]
  email?: string
}

interface RoleDto {
  role: string
  privileges: string
}

interface RoleResponseDto {
  ROLE: string
}

interface PrivilegeResponseDto {
  PRIVILEGE: string
}

interface RoleEdit {
  roleName: string;
  privileges: string;
}

@Component({
  selector: 'app-users',
  standalone: false,
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit, OnDestroy {
  users: User[] = [];
  newUser: User = { username: '', defaultTablespace: '', temporaryTablespace: '', profile: '', quota: '', accountLocked: false };
  selectedUser: User = { username: '', defaultTablespace: '', temporaryTablespace: '', profile: '', quota: '', accountLocked: false };
  showModal: boolean = false;
  modalType: 'create' | 'edit' = 'create';
  roles: RoleDto[] = [];
  selectedRoleToEdit: RoleEdit = { roleName: "", privileges: "" };
  newRole: any = { roleName: '', privileges: '' };
  showRoleModal = false;
  showEditRoleModal = false;
  showEditRolePrivilegesModal = false;
  showAssignRoleModal = false;
  selectedUserRole:string="";
  private destroy$ = new Subject<void>();
  @ViewChild('userModal') userModal!: ElementRef;
  @ViewChild('roleModal') roleModal!: ElementRef;
  @ViewChild('editRoleModal') editRoleModal!: ElementRef;
  @ViewChild('editRolePrivilegeModal') editRolePrivilegeModal!: ElementRef;
  @ViewChild('assignRoleModal') assignRoleModal!: ElementRef;
  constructor(private userService: UserService, private roleService: RoleService) { }

  ngOnInit(): void {
    this.loadUsers();
    this.loadRoles();
  }
  loadUsers() {
    this.userService.getAllUsers().pipe(takeUntil(this.destroy$)).subscribe({
      next: (users) => {
        this.users = users;
        this.users.forEach(user => this.loadUserRoles(user));

      },
      error: (err) => {
        this.handleError(err)
      }
    })
  }
  loadRoles() {
    this.roleService.getAllRoles().pipe(takeUntil(this.destroy$)).subscribe({
      next: (roles: any) => {
        // Ensure that server returns correct formatting like camelcase instead upper case for role naming during all http requests/responses
        this.roles = roles.map((role: RoleResponseDto) => ({ role: role.ROLE.replace('C##', ''), privileges: '' }));

        // then continue with correct lower /camelcase format in front end
        this.roles = this.roles.map(role => ({ ...role, role: role.role.toLowerCase() }))
        this.loadPrivilegesToRoles();

      },
      error: (err) => {
        this.handleError(err)
      }
    })
  }
  loadPrivilegesToRoles() {
    const privilegeObservables = this.roles.map(role =>
      this.roleService.getRolePrivileges(role.role)
    );

    forkJoin(privilegeObservables).pipe(takeUntil(this.destroy$)).subscribe({
      next: (privilegesArrays) => {
        privilegesArrays.forEach((privileges, index) => {
          this.roles[index].privileges = privileges.map((privilege: PrivilegeResponseDto) => privilege.PRIVILEGE).join(',');
        });
      },
      error: (err) => {
        this.handleError(err)
      }
    })
  }
  loadUserRoles(user: User) {
    const usernameWithPrefix = `c##${user.username.toLowerCase()}`;
    this.roleService.getUserRoles(usernameWithPrefix).pipe(takeUntil(this.destroy$)).subscribe({
      next: (userRoles: any) => {
        user.userRoles = userRoles.map((role: any) => role.GRANTED_ROLE);
        if (userRoles && userRoles.length > 0) {
          user.selectedRole = userRoles[0].GRANTED_ROLE;
        } else {
          user.selectedRole = "";
        }
      },
      error: (err) => {
        this.handleError(err)
      }
    })
  }
  openCreateModal() {
    this.selectedUser = { username: '', defaultTablespace: '', temporaryTablespace: '', profile: '', quota: '', accountLocked: false };
    this.modalType = 'create';
    this.userModal.nativeElement.showModal();

  }
  openEditModal(user: User) {
    this.selectedUser = { ...user };
    this.modalType = 'edit';
    this.userModal.nativeElement.showModal();
    this.loadUserRoles(user);
  }
  createUser() {
    this.userService.createUser(this.selectedUser).pipe(takeUntil(this.destroy$)).subscribe({
      next: (user) => {
        this.users.push(user);
        this.showSuccess('User created successfully');
        this.closeModal();
        this.newUser = { username: '', defaultTablespace: '', temporaryTablespace: '', profile: '', quota: '', accountLocked: false };
      },
      error: (err) => {
        this.handleError(err)
      }
    })
  }

  updateUser() {
    if (this.selectedUser.id != null) {
      this.userService.updateUser(this.selectedUser.id, this.selectedUser).pipe(takeUntil(this.destroy$)).subscribe({
        next: (updatedUser) => {
          this.users = this.users.map(user =>
            user.id === updatedUser.id ? updatedUser : user);
          this.showSuccess('User updated successfully');
          this.closeModal();
          this.selectedUser = { username: '', defaultTablespace: '', temporaryTablespace: '', profile: '', quota: '', accountLocked: false };
        },
        error: (err) => {
          this.handleError(err)
        }
      });
    }
  }
  deleteUser(id: number) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'You will not be able to recover this user!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.userService.deleteUser(id).pipe(takeUntil(this.destroy$)).subscribe({
          next: () => {
            this.users = this.users.filter(user => user.id !== id);
            this.showSuccess('User deleted successfully');
          },
          error: (err) => {
            this.showError('Error deleting user', err.message);
          }
        })
      }
    });
  }

  openAssignRoleModal(user:User) {
    this.selectedUser = user;
    this.showAssignRoleModal = true;
    this.selectedUserRole="";
    this.assignRoleModal.nativeElement.showModal();
  }
  closeAssignRoleModal() {
    this.selectedUserRole="";
    this.showAssignRoleModal = false;
    this.assignRoleModal.nativeElement.close();
  }
  assignRole(){
    const usernameWithPrefix =  `${this.selectedUser.username.toLowerCase()}`;
    this.roleService.assignRoleToUser(usernameWithPrefix, (this.selectedUserRole || '').trim().toUpperCase()).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.showSuccess('Role assigned successfully');
        this.loadUserRoles(this.selectedUser);
        this.closeAssignRoleModal()
      },
      error: (err) => {
        this.handleError(err);
      }
    });
  }

  openCreateRoleModal() {
    this.newRole = { roleName: '', privileges: '' };
    this.showRoleModal = true;
    this.roleModal.nativeElement.showModal();
  }
  openEditRoleModal() {
    this.showEditRoleModal = true;
    this.editRoleModal.nativeElement.showModal();
  }
  openEditRolePrivilegesModal(role: RoleDto) {
    this.selectedRoleToEdit = { roleName: role.role.replace('C##', '').toLowerCase(), privileges: role.privileges };
    this.showEditRolePrivilegesModal = true;
    this.editRolePrivilegeModal.nativeElement.showModal();
  }
  closeRoleModal() {
    this.newRole = { roleName: '', privileges: '' };
    this.showRoleModal = false;
    this.roleModal.nativeElement.close();
  }
  closeEditRoleModal() {
    this.showEditRoleModal = false;
    this.editRoleModal.nativeElement.close();
  }
  closeEditRolePrivilegesModal() {
    this.selectedRoleToEdit = { roleName: "", privileges: "" };
    this.showEditRolePrivilegesModal = false;
    this.editRolePrivilegeModal.nativeElement.close();
  }
  createRole() {
    this.roleService.createRole(this.newRole.roleName).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.showSuccess('Role created successfully');
        this.loadRoles();
        this.closeRoleModal();
      },
      error: (err) => {
        this.handleError(err);
      }
    })
  }

  updateRolePrivileges() {
    const privilegesArray = this.selectedRoleToEdit.privileges.split(',').map((privilege: string) => privilege.trim());
    this.roleService.grantPrivilegesToRole(this.selectedRoleToEdit.roleName, privilegesArray).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.showSuccess('Privileges granted successfully');
        this.loadRoles();
        this.closeEditRolePrivilegesModal();
      },
      error: (err) => {
        this.handleError(err);
      }
    })
  }
  revokeRoleFromUser(user: User, role: string) {
    const usernameWithPrefix = `c##${user.username.toLowerCase()}`;
    this.roleService.revokeRoleFromUser(usernameWithPrefix, role).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.showSuccess('Role revoked successfully');
        this.loadUserRoles(user);
      },
      error: (err) => {
        this.handleError(err)
      }
    })
  }
  dropRole(roleName: string) {
    this.roleService.dropRole(roleName).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.showSuccess('Role deleted successfully');
        this.loadRoles();
      },
      error: (err) => {
        this.handleError(err)
      }
    })
  }

  closeModal() {
    this.selectedUser = { username: '', defaultTablespace: '', temporaryTablespace: '', profile: '', quota: '', accountLocked: false };
    this.userModal.nativeElement.close();
  }
  private handleError(err: any) {
    this.showError('Error occurred!', err.message)
  }
  showSuccess(message: string) {
    Swal.fire({
      icon: 'success',
      title: 'Success!',
      text: message,
      timer: 2000,
      showConfirmButton: false
    });
  }
  showError(title: string, message: string) {
    Swal.fire({
      icon: 'error',
      title: title,
      text: message,
    });
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
