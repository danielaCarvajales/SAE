import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule, MatDialog } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NotificationService } from '../../../../services/notification.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { environment } from "../../../../../environments/environment"
import { SendEmailsComponent } from '../send-emails/send-emails.component';

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
export class SeeEmailsComponent implements OnInit {
  remitente: string = '';
  destinatario: string = '';
  asunto: string = '';
  contenido: string | null = null;
  fechaRecibido: string = '';
  adjunto: string | null = null;
  adjuntoId: string | null = null;
  fileName = "";
  fileExtension = "";
  isLoading = false;
  remitenteEmail = "";
  messageId: string | null = null
  references: string | null = null
  inReplyTo: string | null = null;

  constructor(
    private notificationService: NotificationService,
    public dialogRef: MatDialogRef<SeeEmailsComponent>,
    private dialog: MatDialog,

    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    if (!data) {
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
    this.adjuntoId = data.adjuntoId || null;
    this.messageId = data.messageId || null;
    this.references = data.references || null;
    this.inReplyTo = data.inReplyTo || null;

    const emailMatch = this.remitente.match(/<([^>]+)>/);
    if (emailMatch && emailMatch[1]) {
      this.remitenteEmail = emailMatch[1];
    }
    if (this.adjunto && this.adjunto !== 'Sin adjunto') {
      this.fileName = this.adjunto.split(' ')[0];
      const lastDotIndex = this.fileName.lastIndexOf('.');
      if (lastDotIndex !== -1) {
        this.fileExtension = this.fileName.substring(lastDotIndex + 1).toLowerCase();
      }
    }
  }
  ngOnInit(): void { }

  hasAttachment(): boolean {
    return this.adjunto !== null && this.adjunto !== "Sin adjunto" && this.adjuntoId !== null
  }

  hasContent(): boolean {
    return this.contenido !== null && this.contenido.trim() !== ""
  }

 

  getMimeType(extension: string): string {
    const mimeTypes: { [key: string]: string } = {
      doc: "application/msword",
      docx: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      xls: "application/vnd.ms-excel",
      xlsx: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      ppt: "application/vnd.ms-powerpoint",
      pptx: "application/vnd.openxmlformats-officedocument.presentationml.presentation",
      pdf: "application/pdf",
      txt: "text/plain",
      jpg: "image/jpeg",
      jpeg: "image/jpeg",
      png: "image/png",
      gif: "image/gif",
    }

    return mimeTypes[extension] || "application/octet-stream"
  }

  
  downloadAttachment(): void {
    if (!this.adjunto || !this.adjuntoId || this.adjunto === "Sin adjunto") {
      return
    }

    this.isLoading = true
    const url = this.getAttachmentUrl()

    const iframe = document.createElement("iframe")
    iframe.style.display = "none"
    document.body.appendChild(iframe)

    iframe.onload = () => {
      setTimeout(() => {
        document.body.removeChild(iframe)
        this.isLoading = false
        this.notificationService.success(`Archivo "${this.fileName}" descargado correctamente`)
      }, 1000)
    }

    iframe.onerror = () => {
      document.body.removeChild(iframe)
      this.isLoading = false
      this.notificationService.warn("Error al descargar el archivo")
    }

    iframe.src = url
  }

  getAttachmentUrl(): string {
    if (!this.adjunto || !this.adjuntoId || this.adjunto === "Sin adjunto") {
      return "#"
    }

    const baseUrl = environment.API_URL || ""
    // Añadir timestamp para evitar caché
    const timestamp = new Date().getTime()
    return `${baseUrl}/email/attachment/${this.adjuntoId}/${encodeURIComponent(this.fileName)}?t=${timestamp}`
  }

  replyToEmail(): void {

    if (!this.remitenteEmail) {
      this.notificationService.warn("No se pudo determinar el correo del remitente para responder.")
      return
    }

    this.dialogRef.close()

    const replyData = {
      to: this.remitenteEmail,
      subject: `RE: ${this.asunto}`,
      originalContent: this.contenido,
      isReply: true,
      messageId: this.data.messageId || null,
      references: this.data.references || null,
      inReplyTo: this.inReplyTo,
    }

    const dialogRef = this.dialog.open(SendEmailsComponent, {
      width: "600px",
      maxHeight: "60vh",
      position: { bottom: "75px", right: "40px" },
      hasBackdrop: true,
      panelClass: "custom-dialog-container",
      backdropClass: "transparent-backdrop",
      data: replyData,
    })
  }


}