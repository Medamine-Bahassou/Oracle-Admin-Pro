import { Component } from '@angular/core';
import {
  faTachometerAlt, // Dashboard icon
  faUsers,
  faTable,
  faCogs,
  faDatabase,
  faShieldHalved,
  faRightFromBracket
} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-admin',
  standalone:false,
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent {
  icons = {
    dashboard: faTachometerAlt,
    users: faUsers,
    slowQueries: faTable,
    schedule: faCogs,
    backup: faDatabase,
    table: faTable,
    security: faShieldHalved,
    logout: faRightFromBracket
  };
}
