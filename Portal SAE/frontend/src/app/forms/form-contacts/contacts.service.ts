import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { ContactDTO } from '../../interfaces/contact/contact.dto';
import { CreateContactDTO } from '../../interfaces/contact/createContact.dto';
import { UpdateContactDTO } from '../../interfaces/contact/updateContact.dto';
import { NotificationService } from '../../services/notification.service';

@Injectable({
  providedIn: 'root',
})
export class ContactsService {
  constructor(
    private apiService: ApiService,
    private notificationService: NotificationService
  ) {}

  // Formgroup Control
  form: FormGroup = new FormGroup({
    codigo: new FormControl(null),
    numeroIdentificacion: new FormControl('', [
      Validators.maxLength(30),
      Validators.pattern('^[0-9]*$'),
      Validators.required,
    ]),
    nombres: new FormControl('', Validators.required),
    apellidos: new FormControl('', Validators.required),
    pais: new FormControl('', Validators.required),
    departamento: new FormControl('', Validators.required),
    ciudad: new FormControl('', Validators.required),
    direccion: new FormControl('', Validators.required),
    codigoPais: new FormControl('+', [
      Validators.pattern(/^\+[0-9]{1,3}$/),
      Validators.required,
    ]),
    movil: new FormControl('', [
      Validators.pattern('[1-9]{1}[0-9]{9}'),
      Validators.required,
    ]),
    email: new FormControl('', [Validators.email, Validators.required]),
    fechaNacimiento: new FormControl('', [Validators.required]),
    rutaImagen: new FormControl(''),

    tipoIdentificacion: new FormControl(null, Validators.required),
    cuenta: new FormControl(null),
  });

  // Sets form fields to null
  initializeFormGroup() {
    this.form.setValue({
      codigo: null,
      numeroIdentificacion: null,
      nombres: null,
      apellidos: null,
      pais: null,
      departamento: null,
      ciudad: null,
      direccion: null,
      codigoPais: null,
      movil: null,
      email: null,
      fechaNacimiento: null,
      rutaImagen: null,

      tipoIdentificacion: null,
      cuenta: null,
    });
  }

  // Adds entity in database
  async insert(contact: CreateContactDTO): Promise<any> {
    try {
      const response = await this.apiService.post('contacto', contact);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Updates entity in database
  async update(code: number, contact: UpdateContactDTO): Promise<any> {
    try {
      const response = await this.apiService.put(`contacto/${code}`, contact);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Deletes entity in database
  async delete(code: number): Promise<any> {
    try {
      const response = await this.apiService.delete(`contacto/${code}`);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Fills form fields with row data
  populateForm(row: ContactDTO, codeIdentityType: number | undefined) {
    const formData = {
      ...row,
      tipoIdentificacion: codeIdentityType,
    };
    this.form.setValue(formData);
  }
}
