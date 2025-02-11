import { Injectable } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { ActivityDTO } from '../../interfaces/activity/activity.dto';
import { CreateActivityDTO } from '../../interfaces/activity/createActivity.dto';
import { UpdateActivityDTO } from '../../interfaces/activity/updateActivity.dto';
import { NotificationService } from '../../services/notification.service';

@Injectable({
  providedIn: 'root',
})
export class ActivitiesService {
  constructor(
    private apiService: ApiService,
    private notificationService: NotificationService
  ) {}

  // Formgroup Control
  form: FormGroup = new FormGroup({
    codigo: new FormControl(null),
    porcentajeProgreso: new FormControl(0, Validators.required),
    fechaHoraInicio: new FormControl('', [
      Validators.pattern(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/),
      Validators.required,
    ]),
    fechaHoraFin: new FormControl('', [
      Validators.pattern(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/),
      Validators.required,
      this.fechaHoraFinValidator,
    ]),
    descripcion: new FormControl('', [
      Validators.maxLength(300),
      Validators.required,
    ]),
    horasTrabajo: new FormControl(0, Validators.required),

    codigoTarea: new FormControl(null, Validators.required),
    contactoAsignado: new FormControl(null, Validators.required),
    estado: new FormControl(null, Validators.required),
    unidadTrabajo: new FormControl(null, Validators.required),
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
      porcentajeProgreso: 0,
      fechaHoraInicio: null,
      fechaHoraFin: null,
      descripcion: null,
      horasTrabajo: 0,

      codigoTarea: null,
      contactoAsignado: null,
      estado: null,
      unidadTrabajo: null,
    });
  }

  // Adds entity in database
  async insert(activity: CreateActivityDTO): Promise<any> {
    try {
      const response = await this.apiService.post('actividad', activity);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Updates entity in database
  async update(code: number, activity: UpdateActivityDTO): Promise<any> {
    try {
      const response = await this.apiService.put(`actividad/${code}`, activity);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Deletes entity in database
  async delete(code: number): Promise<any> {
    try {
      const response = await this.apiService.delete(`actividad/${code}`);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Fills form fields with row data
  populateForm(
    row: ActivityDTO,
    codeTask: any,
    codeContact: any,
    codeStatus: any,
    codeWorkUnit: any
  ) {
    const formData = {
      ...row,
      codigoTarea: codeTask,
      contactoAsignado: codeContact,
      estado: codeStatus,
      unidadTrabajo: codeWorkUnit,
      porcentajeProgreso: +row.porcentajeProgreso, // Convert to number
    };

    this.form.setValue(formData);
  }
}
