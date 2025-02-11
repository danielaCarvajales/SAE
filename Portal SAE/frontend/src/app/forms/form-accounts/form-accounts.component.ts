import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ApiService } from '../../services/api.service';
import { AccountsService } from './accounts.service';
import { AccountDTO } from '../../interfaces/account/account.dto';

@Component({
  selector: 'app-form-accounts',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatSelectModule,
    MatButtonModule,
  ],
  templateUrl: './form-accounts.component.html',
  styleUrl: './form-accounts.component.css',
})
export class FormAccountsComponent {
  userCode!: number | null;
  userList: any;
  accountsIdentityTypesList: any;
  consultantsList: any;
  servicesTypesList: any;

  constructor(
    public accountsService: AccountsService,
    public apiService: ApiService,
    public dialogRef: MatDialogRef<FormAccountsComponent>,
    @Inject(MAT_DIALOG_DATA) public dialogData: any
  ) {
    this.userCode = dialogData.userCode;
    this.userList = dialogData.userList;
    this.accountsIdentityTypesList = dialogData.accountsIdentityTypesList;
    this.consultantsList = dialogData.consultantsList;
    this.servicesTypesList = dialogData.servicesTypesList;
  }

  // Function to handle form submission
  async onSubmit() {
    this.accountsService.form.get('codigoUsuario')?.setValue(this.userCode);

    if (this.accountsService.form.valid) {
      const formValue: AccountDTO = { ...this.accountsService.form.value };
      const code: number = this.accountsService.form.get('codigo')?.value;

      if (code) {
        // Updating the account
        await this.accountsService.update(code, formValue);
      } else {
        // Inserting a new account
        await this.accountsService.insert(formValue);
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
    this.accountsService.form.reset();
    this.accountsService.initializeFormGroup();
    this.dialogRef.close();
  }
}
