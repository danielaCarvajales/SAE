import { SelectionModel } from '@angular/cdk/collections';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { EventsService } from '../../../forms/form-events/events.service';
import { FormEventsComponent } from '../../../forms/form-events/form-events.component';
import { EventDTO } from '../../../interfaces/event/event.dto';
import { ApiService } from '../../../services/api.service';
import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { UserByRoleDTO } from '../../../interfaces/user/userByRole.dto';
import { RelationshipDTO } from '../../../interfaces/relationship.dto';
import { AccountDTO } from '../../../interfaces/account/account.dto';
import { ContactDTO } from '../../../interfaces/contact/contact.dto';

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    CommonModule,
    FormEventsComponent,
    MatExpansionModule,
  ],
  templateUrl: './events.component.html',
  styleUrl: './events.component.css',
})
export class EventsComponent implements OnInit {
  constructor(
    private eventsService: EventsService,
    private apiService: ApiService,
    private userService: UserService,
    private notificationService: NotificationService,
    public dialog: MatDialog
  ) {}

  userCode!: number | null;
  userRole!: string | null;
  isUserConsultant!: boolean;

  consultantsList: any;
  businessmenList: any;
  eventsTypesList: any;
  eventsStatusList: any;
  eventsVisibilitiesList: any;
  eventsNotifyByList: any;
  accountsList: any;
  contactsList: any = [];

  row!: EventDTO;
  columns!: string[];
  selectedRows = new SelectionModel<EventDTO>(false, []);
  tableDataSource = new MatTableDataSource<EventDTO>([]);

  ngOnInit(): void {
    this.userCode = this.userService.getUserCode();
    this.userRole = this.userService.getUserRole();
    this.isUserConsultant = this.userService.isUserConsultant();
    this.fetchData();
  }

  // Gets data from API
  async fetchData(): Promise<void> {
    this.selectedRows.clear();

    try {
      const apiEndpoints = [
        `evento/usuario/${this.userCode}/${this.userRole}`,
        'usuario/consultor',
        'usuario/empresario',
        'relaciones/tipos_evento',
        'relaciones/estados_evento',
        'relaciones/visibilidades_evento',
        'relaciones/medios_notificacion_evento',
        `cuenta/usuario/${this.userCode}`,
      ];

      const [
        eventsListResponse,
        consultantsListResponse,
        businessmenListResponse,
        eventTypesResponse,
        eventStatusResponse,
        eventsVisibilityResponse,
        eventsNotifyByResponse,
        accountsListResponse,
      ] = await this.apiService.getMultiple(apiEndpoints);

      if (eventsListResponse && eventsListResponse.length > 0) {
        const apiData: EventDTO[] = eventsListResponse;
        // Check if porcentajeProgreso is 100 and update estado accordingly
        apiData.forEach(event => {
          if (event.porcentajeProgreso === '100') {
            event.estado = 'Realizado';
          } else if (event.porcentajeProgreso === '0') {
            event.estado = 'Pendiente';
          }
        });
        this.columns = ['seleccion', ...Object.keys(apiData[0])];
        this.tableDataSource.data = apiData;
      } else {
        this.columns = [];
        this.tableDataSource = new MatTableDataSource<EventDTO>([]);
      }

      [
        this.consultantsList,
        this.businessmenList,
        this.eventsTypesList,
        this.eventsStatusList,
        this.eventsVisibilitiesList,
        this.eventsNotifyByList,
        this.accountsList,
      ] = [
        consultantsListResponse,
        businessmenListResponse,
        eventTypesResponse,
        eventStatusResponse,
        eventsVisibilityResponse,
        eventsNotifyByResponse,
        accountsListResponse,
      ];

      // Iterate through accountsList
      this.contactsList = [];

      for (const account of this.accountsList) {
        try {
          const contactoEndpoint = `contacto/cuenta/${account.codigo}`;
          const contactoResponse = await this.apiService.get(contactoEndpoint);

          this.contactsList.push(...contactoResponse);
        } catch (error) {
          this.notificationService.warn(
            `Fallo al obtener información de la cuenta ${account.codigo}. Error:` +
              error
          );
        }
      }
    } catch (error) {
      this.notificationService.warn(
        'Fallo al obtener información de API! Error: ' + error
      );
    }
  }

  // Function called when a row is clicked
  onRowClick(row: EventDTO, event: Event): void {
    event.stopPropagation();
    this.selectedRows.toggle(row);
    this.row = row;
  }

  // Function to open dialog window for creating or updating an event
  openDialogWithData() {
    const dialogRef = this.dialog.open(FormEventsComponent, {
      disableClose: true,
      autoFocus: true,
      width: '80%',
      height: '80%',
      data: {
        userCode: this.userCode,
        userRole: this.userRole,
        consultantsList: this.consultantsList,
        businessmenList: this.businessmenList,
        eventsTypesList: this.eventsTypesList,
        eventsStatusList: this.eventsStatusList,
        eventsVisibilitiesList: this.eventsVisibilitiesList,
        eventsNotifyByList: this.eventsNotifyByList,
        accountsList: this.accountsList,
        contactsList: this.contactsList,
      },
    });
    dialogRef.afterClosed().subscribe(() => this.fetchData());
  }

  // Function to navigate to the form for creating a new event
  toCreate() {
    this.openDialogWithData();
  }

  // Function to navigate to the form for updating an existing event
  toUpdate() {
    const codeConsultant = this.consultantsList.find(
      (u: UserByRoleDTO) => u.nombre == this.row.codigoConsultor
    ).codigo;
    const codeBusinessman = this.businessmenList.find(
      (u: UserByRoleDTO) => u.nombre == this.row.codigoEmpresario
    ).codigo;
    const codeType = this.eventsTypesList.find(
      (u: RelationshipDTO) => u.nombre === this.row.tipo
    ).codigo;
    const codeStatus = this.eventsStatusList.find(
      (u: RelationshipDTO) => u.nombre === this.row.estado
    ).codigo;
    const codeVisibility = this.eventsVisibilitiesList.find(
      (u: RelationshipDTO) => u.nombre === this.row.visibilidad
    ).codigo;
    const codeNotifyBy = this.eventsNotifyByList.find(
      (u: RelationshipDTO) => u.nombre === this.row.notificarPor
    ).codigo;
    const codeAccountAssigned = this.accountsList.find(
      (u: AccountDTO) => u.paginaWeb === this.row.cuentaAsignada
    ).codigo;
    const codeContactAssigned = this.contactsList.find(
      (u: ContactDTO) =>
        `${u.nombres} ${u.apellidos}` === this.row.contactoAsignado
    ).codigo;
    this.eventsService.populateForm(
      this.row,
      codeConsultant,
      codeBusinessman,
      codeType,
      codeStatus,
      codeVisibility,
      codeNotifyBy,
      codeAccountAssigned,
      codeContactAssigned
    );
    this.openDialogWithData();
  }

  // Function to delete an event
  async toDelete() {
    await this.eventsService.delete(this.row.codigo);
    this.fetchData();
  }
}
