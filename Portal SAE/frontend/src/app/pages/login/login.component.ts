import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { NotificationService } from '../../services/notification.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [MatInputModule, MatFormFieldModule, MatButtonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  constructor(
    private apiService: ApiService,
    private router: Router,
    private notificationService: NotificationService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    if (localStorage.getItem('token') !== null) {
      this.router.navigate(['/hogar']);
    }
  }

  async onSubmit(
    username: string,
    password: string,
    event: Event
  ): Promise<void> {
    event.preventDefault();

    try {
      // Make API call to login
      const response = await this.apiService.post(
        'usuario/inicio_sesion',
        {
          nombre: username,
          contrasena: password,
        },
        false
      );
      // Save undecoded JWT token
      localStorage.setItem('token', response);

      const rol = this.userService.getUserRole();
      const nombreUsuario = this.userService.getUserName();

      // Display success notification and navigate to home page
      this.notificationService.success(
        `¡Bienvenido ${rol}, ${nombreUsuario}!`,
        1000
      );
      this.router.navigate(['/hogar']);
    } catch (error: any) {
      if (error.response && error.response.data) {
        // Error response from server
        this.notificationService.warn(error.response.data);
      } else if (error.message === 'Network Error') {
        // Network error, unable to connect to server
        this.notificationService.warn(
          'No se puede conectar al servidor. Por favor, inténtelo de nuevo más tarde.'
        );
      } else {
        // Generic error
        this.notificationService.warn('Error: ' + error.message);
      }
    }
  }
}
