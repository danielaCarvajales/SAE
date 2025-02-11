import { Injectable } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { CreateIncidentDTO } from '../../interfaces/incident/createIncident.dto';
import { IncidentDTO } from '../../interfaces/incident/incident.dto';
import { UpdateIncidentDTO } from '../../interfaces/incident/updateIncident.dto';
import { NotificationService } from '../../services/notification.service';

@Injectable({
  providedIn: 'root',
})
export class IncidentsService {
  constructor(
    private apiService: ApiService,
    private notificationService: NotificationService
  ) {}

  // Formgroup Control
  form: FormGroup = new FormGroup({
    codigo: new FormControl(null),
    pregunta: new FormControl('', [
      Validators.maxLength(300),
      Validators.required,
    ]),
    respuesta: new FormControl('', [
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
    codigoConsultor: new FormControl(null, Validators.required),
    codigoEmpresario: new FormControl(null, Validators.required),
    categoria: new FormControl(null, Validators.required),
    estado: new FormControl(null, Validators.required),
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
      pregunta: null,
      respuesta: null,
      fechaHoraInicio: null,
      fechaHoraFin: null,

      codigoConsultor: null,
      codigoEmpresario: null,
      categoria: null,
      estado: null,
    });
  }

  // Adds entity in database
  async insert(incident: CreateIncidentDTO): Promise<any> {
    try {
      const response = await this.apiService.post('incidencia', incident);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Updates entity in database
  async update(code: number, incident: UpdateIncidentDTO): Promise<any> {
    try {
      const response = await this.apiService.put(
        `incidencia/${code}`,
        incident
      );
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Deletes entity in database
  async delete(code: number): Promise<any> {
    try {
      const response = await this.apiService.delete(`incidencia/${code}`);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Fills form fields with row data
  populateForm(
    row: IncidentDTO,
    codeIncidentCategory: any,
    codeIncidentStatus: any,
    codeConsultant: any,
    codeBusinessman: any
  ) {
    const formData = {
      ...row,
      categoria: codeIncidentCategory,
      estado: codeIncidentStatus,
      codigoConsultor: codeConsultant,
      codigoEmpresario: codeBusinessman,
    };
    this.form.setValue(formData);
  }
}
