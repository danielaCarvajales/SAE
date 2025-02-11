import { Injectable } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { CreateEventDTO } from '../../interfaces/event/createEvent.dto';
import { EventDTO } from '../../interfaces/event/event.dto';
import { UpdateEventDTO } from '../../interfaces/event/updateEvent.dto';
import { NotificationService } from '../../services/notification.service';

@Injectable({
  providedIn: 'root',
})
export class EventsService {
  constructor(
    private apiService: ApiService,
    private notificationService: NotificationService
  ) {}

  // Formgroup Control
  form: FormGroup = new FormGroup({
    codigo: new FormControl(null),
    asunto: new FormControl('', [
      Validators.maxLength(300),
      Validators.required,
    ]),
    descripcion: new FormControl('', [
      Validators.maxLength(300),
      Validators.required,
    ]),
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
    tiempoDuracion: new FormControl(null),

    codigoConsultor: new FormControl(null, Validators.required),
    codigoEmpresario: new FormControl(null, Validators.required),
    tipo: new FormControl(null, Validators.required),
    estado: new FormControl(null, Validators.required),
    visibilidad: new FormControl(null, Validators.required),
    notificarPor: new FormControl(null, Validators.required),
    cuentaAsignada: new FormControl(null, Validators.required),
    contactoAsignado: new FormControl(null, Validators.required),
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
      porcentajeProgreso: 0,
      fechaHoraInicio: null,
      fechaHoraFin: null,
      tiempoDuracion: null,

      codigoConsultor: null,
      codigoEmpresario: null,
      tipo: null,
      estado: null,
      visibilidad: null,
      notificarPor: null,
      cuentaAsignada: null,
      contactoAsignado: null,
    });
  }

  // Adds entity in database
  async insert(event: CreateEventDTO): Promise<any> {
    try {
      const response = await this.apiService.post('evento', event);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Updates entity in database
  async update(code: number, event: UpdateEventDTO): Promise<any> {
    try {
      const response = await this.apiService.put(`evento/${code}`, event);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Deletes entity in database
  async delete(code: number): Promise<any> {
    try {
      const response = await this.apiService.delete(`evento/${code}`);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Fills form fields with row data
  populateForm(
    row: EventDTO,
    codeConsultant: any,
    codeBusinessman: any,
    codeType: any,
    codeStatus: any,
    codeVisibility: any,
    codeNotifyBy: any,
    codeAccountAssigned: any,
    codeContactAssigned: any
  ) {
    const formData = {
      ...row,
      codigoConsultor: codeConsultant,
      codigoEmpresario: codeBusinessman,
      tipo: codeType,
      estado: codeStatus,
      visibilidad: codeVisibility,
      notificarPor: codeNotifyBy,
      cuentaAsignada: codeAccountAssigned,
      contactoAsignado: codeContactAssigned,
      porcentajeProgreso: +row.porcentajeProgreso, // Convert to number
    };
    this.form.setValue(formData);
  }
}
