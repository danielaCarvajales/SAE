import { CommonModule } from '@angular/common';
import { Component, OnInit, Inject } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { ApiService } from '../../services/api.service';
import { ActivitiesService } from './activities.service';
import { ActivityDTO } from '../../interfaces/activity/activity.dto';

@Component({
  selector: 'app-form-activities',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './form-activities.component.html',
  styleUrl: './form-activities.component.css',
})
export class FormActivitiesComponent implements OnInit {
  taskCode!: number | null;
  userCode!: number | null;
  accountsList: any;
  contactsList: any = [];
  activitiesStatusList: any;
  activitiesWorkUnitList: any;

  constructor(
    public activitiesService: ActivitiesService,
    public apiService: ApiService,
    public dialogRef: MatDialogRef<FormActivitiesComponent>,
    @Inject(MAT_DIALOG_DATA) public dialogData: any
  ) {
    this.taskCode = dialogData.taskCode;
    this.userCode = dialogData.userCode;
    this.accountsList = dialogData.accountsList;
    this.contactsList = dialogData.contactsList;
    this.activitiesStatusList = dialogData.activitiesStatusList;
    this.activitiesWorkUnitList = dialogData.activitiesWorkUnitList;
  }

  ngOnInit() {
    this.activitiesService.form.get('codigoTarea')?.setValue(this.taskCode);
  }

  // Function to generate a range of numbers
  // It's used to generate options for 'Porcentaje de Progreso' select input
  generateRange(start: number, end: number, step: number): number[] {
    const range = [];
    for (let i = start; i <= end; i += step) {
      range.push(i);
    }
    return range;
  }

  // Function to handle form submission
  async onSubmit() {
    if (this.activitiesService.form.valid) {
      const formValue: ActivityDTO = { ...this.activitiesService.form.value };
      const code: number = this.activitiesService.form.get('codigo')?.value;

      if (code) {
        // Updating the activity
        await this.activitiesService.update(code, formValue);
      } else {
        // Inserting a new activity
        await this.activitiesService.insert(formValue);
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
    this.activitiesService.form.reset();
    this.activitiesService.initializeFormGroup();
    this.dialogRef.close();
  }
}
