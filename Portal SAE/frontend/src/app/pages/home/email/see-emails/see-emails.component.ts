import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NotificationService } from '../../../../services/notification.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { environment } from "../../../../../environments/environment"

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
export class SeeEmailsComponent implements OnInit{
  remitente: string = '';
  destinatario: string = '';
  asunto: string = '';
  contenido: string | null = null;
  fechaRecibido: string = '';
  adjunto: string | null = null;
  adjuntoId: string | null = null;
  fileName = "";
  fileExtension = "";



  constructor(
    private notificationService: NotificationService,
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
    this.adjuntoId = data.adjuntoId || null;
    if (this.adjunto && this.adjunto !== 'Sin adjunto') {
      this.fileName = this.adjunto.split(' ')[0];
      const lastDotIndex = this.fileName.lastIndexOf('.');
      if (lastDotIndex !== -1) {
        this.fileExtension = this.fileName.substring(lastDotIndex + 1).toLowerCase();
      }
    }
    }
  ngOnInit(): void {
  }

    hasAttachment(): boolean {
      return this.adjunto !== null && this.adjunto !== "Sin adjunto" && this.adjuntoId !== null
    }
  
    hasContent(): boolean {
      return this.contenido !== null && this.contenido.trim() !== ""
    }

    getAttachmentUrl(): string {
      if (!this.adjunto || !this.adjuntoId || this.adjunto === "Sin adjunto") {
        return "#"
      }

      const baseUrl = environment.API_URL || ""
      return `${baseUrl}/email/attachment/${this.adjuntoId}/${encodeURIComponent(this.fileName)}`
    }



    getMimeType(extension: string): string {
      // Mapeo de extensiones a tipos MIME
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
  
      const url = this.getAttachmentUrl()
  
      // Usar XMLHttpRequest para tener más control sobre la descarga
      const xhr = new XMLHttpRequest()
      xhr.open("GET", url, true)
      xhr.responseType = "blob"
  
      xhr.onload = () => {
        if (xhr.status === 200) {
          let downloadFileName = this.fileName
  
          if (!this.fileExtension && xhr.response.type) {
            const mimeToExt: { [key: string]: string } = {
              "application/msword": ".doc",
              "application/vnd.openxmlformats-officedocument.wordprocessingml.document": ".docx",
              "application/vnd.ms-excel": ".xls",
              "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": ".xlsx",
              "application/vnd.ms-powerpoint": ".ppt",
              "application/vnd.openxmlformats-officedocument.presentationml.presentation": ".pptx",
              "application/pdf": ".pdf",
              "text/plain": ".txt",
              "image/jpeg": ".jpg",
              "image/png": ".png",
              "image/gif": ".gif",
            }
  
            const ext = mimeToExt[xhr.response.type]
            if (ext) {
              downloadFileName += ext
            }
          }
  
          const blob = new Blob([xhr.response], {
            type: this.getMimeType(this.fileExtension),
          })
  
          const blobUrl = window.URL.createObjectURL(blob)
  
          const a = document.createElement("a")
          a.style.display = "none"
          a.href = blobUrl
          a.download = downloadFileName 
  
          document.body.appendChild(a)
          a.click()
  
          window.URL.revokeObjectURL(blobUrl)
          document.body.removeChild(a)
  
          this.notificationService.success(`Archivo "${downloadFileName}" descargado correctamente`)
        } else {
          console.error("Error al descargar:", xhr.statusText)
          this.notificationService.warn("Error al descargar el archivo: " + xhr.statusText)
        }
      }
  
      xhr.onerror = () => {
        console.error("Error de red al descargar el archivo")
        this.notificationService.warn("Error de red al descargar el archivo")
      }
  
      xhr.send()
    }

 
}