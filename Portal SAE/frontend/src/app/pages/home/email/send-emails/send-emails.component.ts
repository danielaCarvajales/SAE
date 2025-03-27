import { Component, OnInit } from '@angular/core';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { ApiService } from '../../../../services/api.service';
import { UserService } from '../../../../services/user.service';
import { FormsModule } from "@angular/forms"
import { CommonModule } from "@angular/common"
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-send-emails',
  standalone: true,
  imports: [
    MatDialogModule,
    FormsModule,
    CommonModule,
    NgIf
  ],
  templateUrl: './send-emails.component.html',
  styleUrl: './send-emails.component.css'
})
export class SendEmailsComponent implements OnInit{
  destinatario = ""
  asunto = ""
  contenido = ""
  archivos: File[] = []
  isLoading = false
  errorMessage = ""
  successMessage = ""

  constructor(
    public apiService: ApiService,
    public userService: UserService,
    private dialogRef: MatDialogRef<SendEmailsComponent>,
  ) {}

  ngOnInit(): void {
    // Inicializar componente
  }

  onFileChange(event: any): void {
    this.archivos = Array.from(event.target.files)
  }

  async onSubmit(event: Event): Promise<void> {
    event.preventDefault()
    this.isLoading = true
    this.errorMessage = ""
    this.successMessage = ""

    try {
      const userEmail = this.userService.getUserEmail()
      const userPasswordObj = localStorage.getItem("email-conf")

      if (!userEmail || !userPasswordObj) {
        throw new Error("No se encontraron las credenciales de correo")
      }
      let userPassword = userPasswordObj
      try {
        const passwordObj = JSON.parse(userPasswordObj)
        if (passwordObj && passwordObj.password) {
          userPassword = passwordObj.password
        }
      } catch (e) {
        console.log("La contraseña no está en formato JSON")
      }

      console.log("Usuario obtenido", userEmail)
      console.log("Contraseña obtenida", userPassword)

      const formData = new FormData()
      formData.append("to", this.destinatario)
      formData.append("subject", this.asunto)
      formData.append("body", this.contenido)
      formData.append("email", userEmail)
      formData.append("password", userPassword)

      if (this.archivos.length > 0) {
        this.archivos.forEach((file) => {
          formData.append("attachments", file)
        })
      }

      console.log("Información enviada", formData)

      const response = await this.sendEmailWithAttachments(formData)

      this.successMessage = "Correo enviado correctamente"
      setTimeout(() => {
        this.dialogRef.close(true)
      }, 2000)
    } catch (error) {
      console.error("Error al enviar el correo:", error)
      this.errorMessage = error instanceof Error ? error.message : "Error al enviar el correo"
    } finally {
      this.isLoading = false
    }
  }

  private async sendEmailWithAttachments(formData: FormData): Promise<any> {
    const token = this.userService.getToken()
    const headers: Record<string, string> = token ? { Authorization: token } : {};

    try {
      const response = await fetch(`${this.apiService.API_URL}/email/send`, {
        method: "POST",
        headers: headers,
        body: formData,
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(errorText || "Error al enviar el correo")
      }

      return await response.text()
    } catch (error) {
      console.error("Error en la petición:", error)
      throw error
    }
  }
}
