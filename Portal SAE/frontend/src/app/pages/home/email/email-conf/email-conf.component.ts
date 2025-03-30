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
    this.router.navigate(['hogar/email']);
  } else {
  }
  
    this.email = this.userService.getUserEmail();
    this.password = this.userService.getUserCode();  
    this.formEmail = this.fb.group({
      email: [this.email ?? '', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });

  }
  
    async onSubmit(): Promise<void> {
  
    if (!this.formEmail.valid) {
      return;
    }
  
    if (this.loading) {
      return;
    }
  
    try {
      this.loading = true;
      const response = await this.apiService.post('email/fetch', this.formEmail.value);
  
      if (response && response.length > 0 && response[0].includes('Imposible acceder')) {
        this.notificationService.warn(response[0]); 
        return;
      }
      this.notificationService.success('Acceso concedido');
      this.saveFormToLocalStorage();
      this.router.navigate(['hogar/email']);
    } catch (error: any) {
      this.notificationService.warn(error.response?.data || 'Error desconocido');
    } finally {
      this.loading = false;
    }
  }


  private saveFormToLocalStorage() {
    localStorage.setItem('email-conf', JSON.stringify(this.formEmail.value));
  }

}