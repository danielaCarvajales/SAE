import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { UpdateAccountDTO } from '../../interfaces/account/updateAccount.dto';
import { AccountDTO } from '../../interfaces/account/account.dto';
import { CreateAccountDTO } from '../../interfaces/account/createAccount.dto';
import { NotificationService } from '../../services/notification.service';

@Injectable({
  providedIn: 'root',
})
export class AccountsService {
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
    email: new FormControl('', [Validators.email, Validators.required]),
    paginaWeb: new FormControl('', Validators.required),
    codigoPais: new FormControl('+', [
      Validators.pattern(/^\+[0-9]{1,3}$/),
      Validators.required,
    ]),
    telefonoFijo: new FormControl('', [
      Validators.pattern('[0-9]{1,15}'),
      Validators.required,
    ]),
    telefonoSecundario: new FormControl('', [
      Validators.pattern('[0-9]{1,15}'),
    ]),
    sectorEconomico: new FormControl('', Validators.required),
    pais: new FormControl('', Validators.required),
    departamento: new FormControl('', Validators.required),
    ciudad: new FormControl('', Validators.required),
    numeroEmpleados: new FormControl(0, Validators.required),

    codigoUsuario: new FormControl(null),
    tipoIdentificacion: new FormControl(null, Validators.required),
    codigoConsultorAsignado: new FormControl(null, Validators.required),
    tipoServicio: new FormControl(null, Validators.required),
  });

  // Sets form fields to null
  initializeFormGroup() {
    this.form.setValue({
      codigo: null,
      numeroIdentificacion: null,
      email: null,
      paginaWeb: null,
      codigoPais: null,
      telefonoFijo: null,
      telefonoSecundario: null,
      sectorEconomico: null,
      pais: null,
      departamento: null,
      ciudad: null,
      numeroEmpleados: null,

      codigoUsuario: null,
      tipoIdentificacion: null,
      codigoConsultorAsignado: null,
      tipoServicio: null,
    });
  }

  // Adds entity in database
  async insert(account: CreateAccountDTO): Promise<any> {
    try {
      const response = await this.apiService.post('cuenta', account);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Updates entity in database
  async update(code: number, account: UpdateAccountDTO): Promise<any> {
    try {
      const response = await this.apiService.put(`cuenta/${code}`, account);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Deletes entity in database
  async delete(code: number): Promise<any> {
    try {
      const response = await this.apiService.delete(`cuenta/${code}`);
      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    }
  }

  // Fills form fields with row data
  populateForm(
    row: AccountDTO,
    codeUser: number | undefined,
    codeIdentityType: number | undefined,
    codeConsultant: number | undefined,
    codeTypeService: number | undefined
  ) {
    const formData = {
      ...row,
      tipoIdentificacion: codeIdentityType,
      tipoServicio: codeTypeService,
      codigoUsuario: codeUser,
      codigoConsultorAsignado: codeConsultant,
    };
    this.form.setValue(formData);
  }
}
