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
import { UserService } from '../../services/user.service';
import { TasksService } from './tasks.service';
import { TaskDTO } from '../../interfaces/task/task.dto';

@Component({
  selector: 'app-form-tasks',
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
  templateUrl: './form-tasks.component.html',
  styleUrl: './form-tasks.component.css',
})
export class FormTasksComponent implements OnInit {
  userCode!: number | null;
  userRole!: string;
  isUserConsultant!: boolean;
  isUserBusinessman!: boolean;
  accountsList: any;
  consultantsList: any;
  businessmenList: any;
  taskStatusList: any;
  taskTypesList: any;

  constructor(
    public tasksService: TasksService,
    public apiService: ApiService,
    private userService: UserService,
    public dialogRef: MatDialogRef<FormTasksComponent>,
    @Inject(MAT_DIALOG_DATA) public dialogData: any
  ) {
    this.userCode = dialogData.userCode;
    this.userRole = dialogData.userRole;
    this.accountsList = dialogData.accountsList;
    this.consultantsList = dialogData.consultantsList;
    this.businessmenList = dialogData.businessmenList;
    this.taskStatusList = dialogData.taskStatusList;
    this.taskTypesList = dialogData.taskTypesList;
  }

  ngOnInit(): void {
    this.isUserConsultant = this.userService.isUserConsultant();
    this.isUserBusinessman = this.userService.isUserBusinessman();
    if (this.isUserConsultant) {
      this.tasksService.form.get('consultorAsignado')?.setValue(this.userCode);
    } else {
      this.tasksService.form.get('empresarioAsignado')?.setValue(this.userCode);
    }
  }

  // Function to generate a range of numbers based on start, end, and step values
  // Range for 'Porcentaje de Progreso'
  generateRange(start: number, end: number, step: number): number[] {
    const range = [];
    for (let i = start; i <= end; i += step) {
      range.push(i);
    }
    return range;
  }

  // Function to handle form submission
  async onSubmit() {
    if (this.isUserConsultant) {
      this.tasksService.form
        .get('codigoConsultorAsignado')
        ?.setValue(this.userCode);
    } else {
      this.tasksService.form
        .get('codigoEmpresarioAsignado')
        ?.setValue(this.userCode);
    }

    if (this.tasksService.form.valid) {
      const formValue: TaskDTO = { ...this.tasksService.form.value };
      const code: number = this.tasksService.form.get('codigo')?.value;

      if (code) {
        // Updating the task
        await this.tasksService.update(code, formValue);
      } else {
        // Inserting a new task
        await this.tasksService.insert(formValue);
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
    this.tasksService.form.reset();
    this.tasksService.initializeFormGroup();
    this.dialogRef.close();
  }
}
