import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
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
export class SendEmailsComponent implements OnInit {
  destinatariosInput = ""
  destinatarios: string[] = []
  asunto = ""
  contenido = ""
  archivos: File[] = []
  isLoading = false
  errorMessage = ""
  successMessage = ""
  isRepy = false
  inReplyTo: string | null = null
  references: string | null = null;

  constructor(
    public apiService: ApiService,
    public userService: UserService,
    private dialogRef: MatDialogRef<SendEmailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any

  ) { }

  ngOnInit(): void {
    this.isRepy = (this.data && this.data.isReply) || false

    if (this.isRepy) {
      if (this.data.to) {
        this.destinatarios = [this.data.to]
      }

      if (this.data.subject) {
        this.asunto = this.data.subject
      } else if (this.data.asuntoOriginal) {
        this.asunto = `RE: ${this.data.asuntoOriginal}`
      }

      this.inReplyTo = this.data.messageId || null
      this.references = this.data.references || null

      if (this.data.contenidoOriginal || this.data.originalContent) {
       
        setTimeout(() => {
          const textareaElement = document.getElementById("contenido-textarea")
          if (textareaElement) {
            textareaElement.focus()
            const textarea = textareaElement as HTMLTextAreaElement
            textarea.setSelectionRange(0, 0)
          }
        }, 100)
      }
    }
  }

  onFileChange(event: any): void {
    const files: FileList = event.target.files;
    if (files) {
      for (let i = 0; i < files.length; i++) {
        this.archivos.push(files[i]);
      }
    }
  }

  async onSubmit(event: Event): Promise<void> {
    event.preventDefault();

    if (this.destinatarios.length === 0) {
      this.errorMessage = "Debes agregar al menos un destinatario";
      return;
    }

    if (!this.asunto) {
      this.errorMessage = "El asunto es obligatorio";
      return;
    }

    if (!this.contenido) {
      this.errorMessage = "El contenido del correo es obligatorio";
      return;
    }

    this.isLoading = true;
    this.errorMessage = "";
    this.successMessage = "";

    try {
      const userEmail = this.userService.getUserEmail();
      const userPasswordObj = localStorage.getItem("email-conf");

      if (!userEmail || !userPasswordObj) {
        throw new Error("No se encontraron las credenciales de correo");
      }

      let userPassword = userPasswordObj;
      try {
        const passwordObj = JSON.parse(userPasswordObj);
        if (passwordObj && passwordObj.password) {
          userPassword = passwordObj.password;
        }
      } catch (e) {
        throw e

      }

      for (const destinatario of this.destinatarios) {
        const formData = new FormData();
        formData.append("to", destinatario);
        formData.append("subject", this.asunto);
        formData.append("body", this.contenido);
        formData.append("email", userEmail);
        formData.append("password", userPassword);

         if (this.isRepy) {
          if (this.inReplyTo) {
            formData.append("inReplyTo", this.inReplyTo)
            console.log("Agregando inReplyTo:", this.inReplyTo)
          }

          if (this.references) {
            let refsValue = this.references
            if (this.inReplyTo && !refsValue.includes(this.inReplyTo)) {
              refsValue = `${refsValue} ${this.inReplyTo}`
            }
            formData.append("references", refsValue)
            console.log("Agregando references:", refsValue)
          } else if (this.inReplyTo) {
            formData.append("references", this.inReplyTo)
            console.log("Agregando references (desde inReplyTo):", this.inReplyTo)
          }
        }

        this.archivos.forEach((file, index) => {
          formData.append(`attachments`, file, file.name);
        });
        await this.sendEmailWithAttachments(formData);
      }

      this.successMessage = "Correo enviado correctamente";
      setTimeout(() => {
        this.dialogRef.close(true);
      }, 2000);
    } catch (error) {
      this.errorMessage = error instanceof Error ? error.message : "Error al enviar el correo";
    } finally {
      this.isLoading = false;
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
      throw error
    }
  }


  onChangeRecipient(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.destinatariosInput = inputElement.value;
    if (this.destinatariosInput.endsWith(',') || this.destinatariosInput.endsWith(' ')) {
      this.processEmails();
    }
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' || event.key === 'Tab' || event.key === ',') {
      event.preventDefault();
      this.processEmails();
    }
  }

  processEmails(): void {
    const emailsArray = this.destinatariosInput
      .split(/[,\s]+/)
      .map(email => email.trim())
      .filter(email => email.length > 0);

    emailsArray.forEach(email => {
      if (this.validateEmail(email) && !this.destinatarios.includes(email)) {
        this.destinatarios.push(email);
      }
    });
    this.destinatariosInput = '';
  }

  validateEmail(email: string): boolean {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  }

  removeDestinatario(email: string): void {
    this.destinatarios = this.destinatarios.filter(e => e !== email);
  }

  onCancel(): void {
    this.dialogRef.close()
  }
}
