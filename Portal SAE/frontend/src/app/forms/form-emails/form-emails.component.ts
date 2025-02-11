import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-form-emails',
  standalone: true,
  imports: [MatButtonModule, MatIconModule],
  templateUrl: './form-emails.component.html',
  styleUrl: './form-emails.component.css',
})
export class FormEmailsComponent {
  /* This component is specifically designed to display the inner HTML content of an email.
   * It is intended for use within a dialog window in the context of visualizing email content.
   * It provides a simple interface to view the email content and allows users to close the dialog window.
   */

  // Content variable to hold email content
  content!: string;

  constructor(
    public dialogRef: MatDialogRef<FormEmailsComponent>,
    @Inject(MAT_DIALOG_DATA) public dialogData: any
  ) {
    this.content = dialogData.content;
  }

  // Function to close the dialog window
  closeDialog() {
    this.dialogRef.close();
  }
}
