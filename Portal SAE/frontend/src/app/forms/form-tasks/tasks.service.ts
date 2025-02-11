import { Injectable } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { CreateTaskDTO } from '../../interfaces/task/createTask.dto';
import { TaskDTO } from '../../interfaces/task/task.dto';
import { UpdateTaskDTO } from '../../interfaces/task/updateTask.dto';
import { NotificationService } from '../../services/notification.service';

@Injectable({
  providedIn: 'root',
})
export class TasksService {
  constructor(
    private apiService: ApiService,
    private notificationService: NotificationService
  ) {}

  // Formgroup Control
  form: FormGroup = new FormGroup({
    codigo: new FormControl(null),
    asunto: new FormControl('', [
      Validators.maxLength(100),
      Validators.required,
    ]),
    descripcion: new FormControl('', [
      Validators.maxLength(300),
      Validators.required,
    ]),
    observacion: new FormControl('', [
      Validators.maxLength(300),
      Validators.required,
    ]),
    fechaHoraInicio: new FormControl('', [
      Validators.pattern(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/),
      Validators.required,
    ]),
    fechaHoraFin: new FormControl('', [
      Validators.pattern(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/),
      Validators.required,
      this.fechaHoraFinValidator,
    ]),
    porcentajeProgreso: new FormControl(0, Validators.required),

    cuentaAsignada: new FormControl(null, Validators.required),
    consultorAsignado: new FormControl(null, Validators.required),
    empresarioAsignado: new FormControl(null, Validators.required),
    estado: new FormControl(null, Validators.required),
    tipo: new FormControl(null, Validators.required),
  });

  // Custom Validator for date validation
  fechaHoraFinValidator(
    control: AbstractControl
  ): { [key: string]: boolean } | null {
    const fechaHoraInicio = control.parent?.get('fechaHoraInicio')?.value;
    const fechaHoraFin = control.value;

    if (fechaHoraFin && fechaHoraInicio && fechaHoraFin <= fechaHoraInicio) {
      return { fechaHoraFinDebeSerMayor: true };
    }
    return null;
  }

  // Sets form fields to null
  initializeFormGroup() {
    this.form.setValue({
      codigo: null,
      asunto: null,
      descripcion: null,
      observacion: null,
      fechaHoraInicio: null,
      fechaHoraFin: null,
      porcentajeProgreso: 0,

      cuentaAsignada: null,
      consultorAsignado: null,
      empresarioAsignado: null,
      estado: null,
      tipo: null,
    });
  }

  // Adds entity in database
  async insert(task: CreateTaskDTO): Promise<any> {
    try {
      const response = await this.apiService.post('tarea', task);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Updates entity in database
  async update(code: number, task: UpdateTaskDTO): Promise<any> {
    try {
      const response = await this.apiService.put(`tarea/${code}`, task);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Deletes entity in database
  async delete(code: number): Promise<any> {
    try {
      const response = await this.apiService.delete(`tarea/${code}`);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Fills form fields with row data
  populateForm(
    row: TaskDTO,
    codeAccount: any,
    codeConsultant: any,
    codeBusinessman: any,
    codeStatus: any,
    codeType: any
  ) {
    const formData = {
      ...row,
      cuentaAsignada: codeAccount,
      consultorAsignado: codeConsultant,
      empresarioAsignado: codeBusinessman,
      estado: codeStatus,
      tipo: codeType,
      porcentajeProgreso: +row.porcentajeProgreso, // Convert to number
    };
    this.form.setValue(formData);
  }
}
