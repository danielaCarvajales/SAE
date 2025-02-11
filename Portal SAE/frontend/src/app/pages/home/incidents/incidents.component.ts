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
import { FormIncidentsComponent } from '../../../forms/form-incidents/form-incidents.component';
import { IncidentsService } from '../../../forms/form-incidents/incidents.service';
import { IncidentDTO } from '../../../interfaces/incident/incident.dto';
import { ApiService } from '../../../services/api.service';
import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { UserByRoleDTO } from '../../../interfaces/user/userByRole.dto';
import { RelationshipDTO } from '../../../interfaces/relationship.dto';

@Component({
  selector: 'app-incidents',
  standalone: true,
  imports: [
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    CommonModule,
    FormIncidentsComponent,
    MatExpansionModule,
  ],
  templateUrl: './incidents.component.html',
  styleUrl: './incidents.component.css',
})
export class IncidentsComponent implements OnInit {
  constructor(
    private incidentsService: IncidentsService,
    private apiService: ApiService,
    private userService: UserService,
    private notificationService: NotificationService,
    public dialog: MatDialog
  ) {}

  userCode!: number | null;
  userRole!: string | null;
  incidentsCategoriesList: any;
  incidentsStatusList: any;
  consultantsList: any;
  businessmenList: any;

  row!: IncidentDTO;
  columns!: string[];
  selectedRows = new SelectionModel<IncidentDTO>(false, []);
  tableDataSource = new MatTableDataSource<IncidentDTO>([]);

  ngOnInit(): void {
    this.userCode = this.userService.getUserCode();
    this.userRole = this.userService.getUserRole();
    this.fetchData();
  }

  // Gets data from API
  async fetchData(): Promise<void> {
    this.selectedRows.clear();

    try {
      const apiEndpoints = [
        `incidencia/usuario/${this.userCode}/${this.userRole}`,
        'relaciones/categorias_incidencia',
        'relaciones/estados_incidencia',
        'usuario/consultor',
        'usuario/empresario',
      ];

      const [
        incidentsListResponse,
        incidentsCategoriesResponse,
        incidentsStatusResponse,
        consultantsListResponse,
        businessmenListResponse,
      ] = await this.apiService.getMultiple(apiEndpoints);

      if (incidentsListResponse && incidentsListResponse.length > 0) {
        const apiData: IncidentDTO[] = incidentsListResponse;
        this.columns = ['seleccion', ...Object.keys(apiData[0])];
        this.tableDataSource.data = apiData;
      } else {
        this.columns = [];
        this.tableDataSource = new MatTableDataSource<IncidentDTO>([]);
      }

      [
        this.incidentsCategoriesList,
        this.incidentsStatusList,
        this.consultantsList,
        this.businessmenList,
      ] = [
        incidentsCategoriesResponse,
        incidentsStatusResponse,
        consultantsListResponse,
        businessmenListResponse,
      ];
    } catch (error) {
      this.notificationService.warn(
        'Fallo al obtener información de API!. Error:' + error
      );
    }
  }

  // Function called when a row is clicked
  onRowClick(row: IncidentDTO, event: Event): void {
    event.stopPropagation();
    this.selectedRows.toggle(row);
    this.row = row;
  }

  // Function to open dialog window for creating or updating an incident
  openDialogWithData() {
    const dialogRef = this.dialog.open(FormIncidentsComponent, {
      disableClose: true,
      autoFocus: true,
      width: '80%',
      height: '80%',
      data: {
        userCode: this.userCode,
        userRole: this.userRole,
        incidentsCategoriesList: this.incidentsCategoriesList,
        incidentsStatusList: this.incidentsStatusList,
        consultantsList: this.consultantsList,
        businessmenList: this.businessmenList,
      },
    });
    dialogRef.afterClosed().subscribe(() => this.fetchData());
  }

  // Function to navigate to the form for creating a new incident
  toCreate() {
    this.openDialogWithData();
  }

  // Function to navigate to the form for updating an existing incident
  toUpdate() {
    const codeIncidentCategory = this.incidentsCategoriesList.find(
      (u: RelationshipDTO) => u.nombre == this.row.categoria
    ).codigo;
    const codeIncidentStatus = this.incidentsStatusList.find(
      (u: RelationshipDTO) => u.nombre == this.row.estado
    ).codigo;
    const codeConsultant = this.consultantsList.find(
      (u: UserByRoleDTO) => u.nombre == this.row.codigoConsultor
    ).codigo;
    const codeBusinessman = this.businessmenList.find(
      (u: UserByRoleDTO) => u.nombre == this.row.codigoEmpresario
    ).codigo;
    this.incidentsService.populateForm(
      this.row,
      codeIncidentCategory,
      codeIncidentStatus,
      codeConsultant,
      codeBusinessman
    );
    this.openDialogWithData();
  }
}
