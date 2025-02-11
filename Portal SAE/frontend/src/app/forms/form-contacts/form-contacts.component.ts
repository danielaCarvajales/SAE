import { CommonModule, formatDate } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ApiService } from '../../services/api.service';
import { NotificationService } from '../../services/notification.service';
import { ContactsService } from './contacts.service';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { ContactDTO } from '../../interfaces/contact/contact.dto';

@Component({
  selector: 'app-form-contacts',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
  ],
  templateUrl: './form-contacts.component.html',
  styleUrl: './form-contacts.component.css',
  providers: [provideNativeDateAdapter()],
})
export class FormContactsComponent {
  userCode!: number | null;
  accountCode!: number | null;
  identityTypesList: any;
  imageSelected!: File;

  constructor(
    public contactsService: ContactsService,
    public apiService: ApiService,
    private notificationService: NotificationService,
    public dialogRef: MatDialogRef<FormContactsComponent>,
    @Inject(MAT_DIALOG_DATA) public dialogData: any
  ) {
    this.userCode = dialogData.userCode;
    this.accountCode = dialogData.accountCode;
    this.identityTypesList = dialogData.identityTypesList;
  }

  // Function to handle file selection
  onFileSelected(event: any) {
    this.imageSelected = event.target.files[0];
  }

  // Function to handle form submission
  async onSubmit() {
    try {
      if (!this.imageSelected) {
        // No image selected, proceed with form submission without image processing
        this.submitForm();
        return;
      }

      // Image is selected, proceed with image processing and then form submission
      const response = await this.apiService.postImage(
        'imagen',
        this.imageSelected
      );
      this.contactsService.form.get('rutaImagen')?.setValue(response);

      this.submitForm();
    } catch (error) {
      this.notificationService.warn(
        'Fallo al obtener información de API! Error: ' + error
      );
    }
  }

  // Private function to handle form submission after image processing
  private async submitForm() {
    this.contactsService.form.get('cuenta')?.setValue(this.accountCode);

    // Formatting date value for 'fechaNacimiento'
    const selectedDate =
      this.contactsService.form.get('fechaNacimiento')?.value;
    const formattedDate = formatDate(selectedDate, 'yyyy-MM-dd', 'en-US');

    // Setting formatted date in the form
    this.contactsService.form.get('fechaNacimiento')?.setValue(formattedDate);

    if (this.contactsService.form.valid) {
      const formValue: ContactDTO = { ...this.contactsService.form.value };
      const code: number = this.contactsService.form.get('codigo')?.value;

      if (code) {
        // Updating the contact
        await this.contactsService.update(code, formValue);
      } else {
        // Inserting a new contact
        await this.contactsService.insert(formValue);
      }

      this.resetFormAndCloseDialog();
    }
  }

  // Function to handle dialog close
  onClose() {
    this.resetFormAndCloseDialog();
  }

  // Function to handle cancellation
  onCancel() {
    this.resetFormAndCloseDialog();
  }

  // Private function to reset form and close dialog
  private resetFormAndCloseDialog() {
    this.contactsService.form.reset();
    this.contactsService.initializeFormGroup();
    this.dialogRef.close();
  }
}
