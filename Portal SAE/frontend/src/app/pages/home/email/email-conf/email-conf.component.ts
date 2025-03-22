import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { UserService } from '../../../../services/user.service';
import { ApiService } from '../../../../services/api.service';
import { NotificationService } from '../../../../services/notification.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-email-conf',
  standalone: true,
  imports: [CommonModule, MatInputModule, ReactiveFormsModule, MatButtonModule],
  templateUrl: './email-conf.component.html',
  styleUrls: ['./email-conf.component.css']
})
export class EmailConfComponent implements OnInit {
  ports: { name: string; value: string }[];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private userService: UserService,
    private apiService: ApiService,
    private notificationService: NotificationService
  ) {
    this.ports = [];
  }

  formEmail!: FormGroup;
  loading: boolean = false;
  email!: string | null;
  password!: number | null;

  ngOnInit(): void {
    const emailConf = localStorage.getItem('email-conf');
  if (emailConf) {
    console.log('Redirigiendo a /hogar/email');
    this.router.navigate(['hogar/email']);
  } else {
    console.log('No hay configuración de email en localStorage');
  }
  
    this.email = this.userService.getUserEmail();
    this.password = this.userService.getUserCode();
  
    console.log('Usuario Email:', this.email);
    console.log('usuario contraseña:', this.password);
  
    this.formEmail = this.fb.group({
      email: [this.email ?? '', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  
    console.log('Formulario creado:', this.formEmail.value);
    console.log('Estado del formulario:', this.formEmail.valid);
  }
  
  

  // Handles form submission, sends form data to API, and redirects on success.
  async onSubmit(): Promise<void> {
    console.log('Intentando enviar formulario...');
  
    if (!this.formEmail.valid) {
      console.log('Formulario no válido:', this.formEmail.errors);
      return;
    }
  
    if (this.loading) {
      console.log('Ya se está procesando una petición...');
      return;
    }
  
    try {
      this.loading = true;
      console.log('Datos enviados al API:', this.formEmail.value);
  
      const response = await this.apiService.post('email/fetch', this.formEmail.value);
      console.log('Respuesta del servidor:', response);
  
      if (response && response.length > 0 && response[0].includes('Imposible acceder')) {
        console.log('Acceso denegado:', response[0]);
        this.notificationService.warn(response[0]); 
        return;
      }
      this.notificationService.success('Acceso concedido');
      this.saveFormToLocalStorage();
      console.log('Redirigiendo a /hogar/email');
      this.router.navigate(['hogar/email']);
    } catch (error: any) {
      console.error('Error en la API:', error);
      this.notificationService.warn(error.response?.data || 'Error desconocido');
    } finally {
      this.loading = false;
    }
  }
  
  


  private saveFormToLocalStorage() {
    localStorage.setItem('email-conf', JSON.stringify(this.formEmail.value));
  }

}