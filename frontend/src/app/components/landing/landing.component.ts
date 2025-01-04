import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface LoginData {
  email: string;
  password: string;
}

@Component({
  selector: 'app-landing',
   standalone: false, // Removed standalone
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss'
})
export class LandingComponent {
  loginData: LoginData = {
    email: '',
    password: ''
  };
  errorMessage: string | null = null;
  constructor(private router: Router) {}

  login() {
    if (this.loginData.email === 'admin' && this.loginData.password === 'admin') {
      // Simulate successful login and navigate to the admin panel
      this.router.navigate(['/admin']);
      // Assuming you have a toast library that shows success messages
      this.showSuccessToast('Login successful');
    } else {
      this.errorMessage = 'Invalid credentials. Please try again.';
      this.showErrorToast(this.errorMessage);
    }
  }

  showSuccessToast(message: string) {
    const toast = document.createElement('div');
    toast.classList.add('toast', 'toast-end');
    const innerToast = document.createElement('div');
    innerToast.classList.add('alert', 'alert-success');
    innerToast.innerHTML = `<span>${message}</span>`;
    toast.appendChild(innerToast);
    document.body.appendChild(toast);
    setTimeout(() => {
      toast.remove();
    }, 3000);
  }

  showErrorToast(message: string) {
    const toast = document.createElement('div');
    toast.classList.add('toast', 'toast-end');
    const innerToast = document.createElement('div');
    innerToast.classList.add('alert', 'alert-error');
    innerToast.innerHTML = `<span>${message}</span>`;
    toast.appendChild(innerToast);
    document.body.appendChild(toast);
    setTimeout(() => {
      toast.remove();
    }, 3000);
  }
}
