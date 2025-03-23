import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NotificationService } from '../../../../services/notification.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-see-emails',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    HttpClientModule 
  ],
  templateUrl: './see-emails.component.html',
  styleUrl: './see-emails.component.css'
})
export class SeeEmailsComponent {
  remitente: string = '';
  destinatario: string = '';
  asunto: string = '';
  contenido: string | null = null; // El contenido es opcional
  fechaRecibido: string = '';
  adjunto: string | null = null; // El adjunto es opcional

  constructor(
    private notificationService: NotificationService,
    private http: HttpClient,
    public dialogRef: MatDialogRef<SeeEmailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    console.log("Datos recibidos en el diálogo:", data);
    if (!data) {
      console.error("El correo es null o undefined");
      this.notificationService.warn("El correo es inválido.");
      this.dialogRef.close();
      return;
    }

    this.remitente = data.remitente || 'Desconocido';
    this.destinatario = data.destinatario || 'No especificado';
    this.asunto = data.asunto || 'Sin asunto';
    this.contenido = data.contenido || null; 
    this.fechaRecibido = data.fechaRecibido || 'Fecha desconocida';
    this.adjunto = data.adjunto || null; 
  }

 

  closeDialog(): void {
    this.dialogRef.close();
  }

}