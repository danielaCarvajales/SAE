import { SelectionModel } from '@angular/cdk/collections';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { AccountsService } from '../../../forms/form-accounts/accounts.service';
import { FormAccountsComponent } from '../../../forms/form-accounts/form-accounts.component';
import { AccountDTO } from '../../../interfaces/account/account.dto';
import { ApiService } from '../../../services/api.service';
import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { UsersDTO } from '../../../interfaces/user/users.dto';
import { RelationshipDTO } from '../../../interfaces/relationship.dto';
import { UserByRoleDTO } from '../../../interfaces/user/userByRole.dto';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    CommonModule,
    FormAccountsComponent,
  ],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.css',
})
export class AccountsComponent implements OnInit {
  constructor(
    private apiService: ApiService,
    private userService: UserService,
    private accountsService: AccountsService,
    private notificationService: NotificationService,
    private router: Router,
    public dialog: MatDialog
  ) {}

  /* VARIABLES  */

  userCode!: number | null;
  usersList: any;
  accountsIdentityTypesList: any;
  consultantsList: any;
  servicesTypesList: any;

  row!: AccountDTO;
  columns!: string[];
  selectedRows = new SelectionModel<AccountDTO>(false, []);
  tableDataSource = new MatTableDataSource<AccountDTO>([]);

  /* FUNCTIONS  */

  ngOnInit(): void {
    this.userCode = this.userService.getUserCode();
    this.fetchData();
  }

  // Function to fetch data from API
  async fetchData(): Promise<void> {
    this.selectedRows.clear();

    try {
      const apiEndpoints = [
        `cuenta/usuario/${this.userCode}`,
        'usuario',
        'relaciones/tipos_identificacion',
        'usuario/consultor',
        'relaciones/tipos_servicio_cuenta',
      ];
      const [
        accountsListResponse,
        usersListResponse,
        identityTypesResponse,
        consultantsListResponse,
        accountsTypeServicesResponse,
      ] = await this.apiService.getMultiple(apiEndpoints);

      // Processing the account data response
      if (accountsListResponse && accountsListResponse.length > 0) {
        const apiData: AccountDTO[] = accountsListResponse;
        this.columns = ['seleccion', ...Object.keys(apiData[0])];
        this.tableDataSource.data = apiData;
      } else {
        this.columns = [];
        this.tableDataSource = new MatTableDataSource<AccountDTO>([]);
      }

      // Processing the foreign keys mapping responses
      this.usersList = usersListResponse;
      this.accountsIdentityTypesList = identityTypesResponse;
      this.consultantsList = consultantsListResponse;
      this.servicesTypesList = accountsTypeServicesResponse;
    } catch (error) {
      this.notificationService.warn(
        'Fallo al obtener información de API! Error: ' + error
      );
    }
  }

  // Function called when a row is clicked
  onRowClick(row: AccountDTO, event: Event): void {
    this.row = row;
    event.stopPropagation();
    this.selectedRows.toggle(row);
  }

  // Function to open dialog window for creating or updating an account
  openDialogWithData() {
    const dialogRef = this.dialog.open(FormAccountsComponent, {
      disableClose: true,
      autoFocus: true,
      width: '80%',
      height: '80%',
      data: {
        userCode: this.userCode,
        usersList: this.usersList,
        accountsIdentityTypesList: this.accountsIdentityTypesList,
        consultantsList: this.consultantsList,
        servicesTypesList: this.servicesTypesList,
      },
    });

    dialogRef.afterClosed().subscribe(() => this.fetchData());
  }

  // Function to navigate to the form for creating a new account
  toCreate() {
    this.openDialogWithData();
  }

  // Function to navigate to the form for updating an existing account
  toUpdate() {
    const codeUser = this.usersList.find(
      (u: UsersDTO) => u.nombre === this.row.codigoUsuario
    ).codigo;
    const codeIdentityType = this.accountsIdentityTypesList.find(
      (u: RelationshipDTO) => u.nombre === this.row.tipoIdentificacion
    ).codigo;
    const codeConsultant = this.consultantsList.find(
      (u: UserByRoleDTO) => u.nombre === this.row.codigoConsultorAsignado
    ).codigo;
    const codeTypeService = this.servicesTypesList.find(
      (u: RelationshipDTO) => u.nombre === this.row.tipoServicio
    ).codigo;

    this.accountsService.populateForm(
      this.row,
      codeUser,
      codeIdentityType,
      codeConsultant,
      codeTypeService
    );

    this.openDialogWithData();
  }

  // Function to delete an account
  async toDelete() {
    await this.accountsService.delete(this.row.codigo);
    this.fetchData();
  }

  // Function to navigate to the contacts page for the selected account
  toContacts() {
    this.router.navigate([`hogar/cuentas/contactos/${this.row.codigo}`]);
  }
}
