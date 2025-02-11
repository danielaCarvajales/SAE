import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { UserService } from '../../../../services/user.service';
import { ApiService } from '../../../../services/api.service';
import { NotificationService } from '../../../../services/notification.service';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';

@Component({
  selector: 'app-email-conf',
  standalone: true,
  imports: [
    CommonModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './email-conf.component.html',
  styleUrl: './email-conf.component.css',
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

  form!: FormGroup;
  loading: boolean = false;
  userEmail!: string | null;
  userCode!: number | null;

  ngOnInit(): void {
    const emailConf = this.userService.getEmailConf();
    if (emailConf) {
      this.router.navigate(['hogar/email']);
    }

    this.userEmail = this.userService.getUserEmail();
    this.userCode = this.userService.getUserCode();

    this.form = this.fb.group({
      anfitrion: ['', [Validators.required]],
      correo: [this.userEmail, [Validators.required, Validators.email]],
      contrasena: ['', [Validators.required]],
      protocolo: ['', [Validators.required]],
      puerto: ['', [Validators.required]],
      codigoUsuario: [this.userCode],
    });
  }

  // Handles form submission, sends form data to API, and redirects on success.
  async onSubmit(event: Event): Promise<void> {
    event.preventDefault();
    if (this.form.valid && !this.loading) {
      try {
        this.loading = true;
        const response = await this.apiService.post(
          'email/capturar',
          this.form.value
        );
        this.notificationService.success(response);
        this.saveFormToLocalStorage();
        this.router.navigate(['hogar/email']);
      } catch (error: any) {
        this.notificationService.warn(error.response.data);
      } finally {
        this.loading = false;
      }
    }
  }

  // Saves form data to local storage.
  private saveFormToLocalStorage() {
    localStorage.setItem('email-conf', JSON.stringify(this.form.value));
  }

  // Updates available ports based on selected protocol.
  updatePorts() {
    const protocol = this.form.get('protocolo')?.value;
    switch (protocol) {
      case 'imap':
        this.ports = [
          { name: 'Puerto 143', value: '143' },
          { name: 'Puerto 993', value: '993' },
        ];
        break;
      case 'pop3':
        this.ports = [
          { name: 'Puerto 110', value: '110' },
          { name: 'Puerto 995', value: '995' },
        ];
        break;
      case 'smtp':
        this.ports = [
          { name: 'Puerto 25', value: '25' },
          { name: 'Puerto 587', value: '587' },
          { name: 'Puerto 465', value: '465' },
        ];
        break;
      default:
        this.ports = [];
        break;
    }
  }
}
