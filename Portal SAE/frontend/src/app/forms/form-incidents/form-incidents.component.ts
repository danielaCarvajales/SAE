import { CommonModule } from '@angular/common';
import { Component, OnInit, Inject } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ApiService } from '../../services/api.service';
import { UserService } from '../../services/user.service';
import { IncidentsService } from './incidents.service';
import { IncidentDTO } from '../../interfaces/incident/incident.dto';

@Component({
  selector: 'app-form-incidents',
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
  templateUrl: './form-incidents.component.html',
  styleUrl: './form-incidents.component.css',
})
export class FormIncidentsComponent implements OnInit {
  userCode!: number | null;
  userRole!: string | null;
  isUserConsultant!: boolean;
  incidentsCategoriesList: any;
  incidentsStatusList: any;
  consultantsList: any;
  businessmenList: any;

  constructor(
    public incidentsService: IncidentsService,
    private userService: UserService,
    public apiService: ApiService,
    public dialogRef: MatDialogRef<FormIncidentsComponent>,
    @Inject(MAT_DIALOG_DATA) public dialogData: any
  ) {
    this.userCode = dialogData.userCode;
    this.userRole = dialogData.userRole;
    this.incidentsCategoriesList = dialogData.incidentsCategoriesList;
    this.incidentsStatusList = dialogData.incidentsStatusList;
    this.consultantsList = dialogData.consultantsList;
    this.businessmenList = dialogData.businessmenList;
  }

  ngOnInit() {
    this.isUserConsultant = this.userService.isUserConsultant();
    if (this.isUserConsultant) {
      this.incidentsService.form
        .get('codigoConsultor')
        ?.setValue(this.userCode);
    } else {
      this.incidentsService.form
        .get('codigoEmpresario')
        ?.setValue(this.userCode);
    }
  }

  // Function to handle form submission
  async onSubmit() {
    if (this.incidentsService.form.valid) {
      const formValue: IncidentDTO = { ...this.incidentsService.form.value };
      const code: number = this.incidentsService.form.get('codigo')?.value;

      if (code) {
        // Updating the incident
        await this.incidentsService.update(code, formValue);
      } else {
        // Inserting a new incident
        await this.incidentsService.insert(formValue);
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
    this.incidentsService.form.reset();
    this.incidentsService.initializeFormGroup();
    this.dialogRef.close();
  }
}
