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
import { Router, ActivatedRoute } from '@angular/router';
import { ActivitiesService } from '../../../../forms/form-activities/activities.service';
import { FormActivitiesComponent } from '../../../../forms/form-activities/form-activities.component';
import { ActivityDTO } from '../../../../interfaces/activity/activity.dto';
import { ApiService } from '../../../../services/api.service';
import { NotificationService } from '../../../../services/notification.service';
import { UserService } from '../../../../services/user.service';
import { ContactDTO } from '../../../../interfaces/contact/contact.dto';
import { RelationshipDTO } from '../../../../interfaces/relationship.dto';

@Component({
  selector: 'app-activities',
  standalone: true,
  imports: [
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    CommonModule,
    FormActivitiesComponent,
    MatExpansionModule,
  ],
  templateUrl: './activities.component.html',
  styleUrl: './activities.component.css',
})
export class ActivitiesComponent implements OnInit {
  constructor(
    private activitiesService: ActivitiesService,
    private userService: UserService,
    private apiService: ApiService,
    private router: Router,
    private route: ActivatedRoute,
    private notificationService: NotificationService,
    public dialog: MatDialog
  ) {}

  taskCode!: number;
  userRole!: string | null;
  userCode!: number | null;
  isUserConsultant!: boolean;
  accountsList: any;
  contactsList: any = [];
  activitiesStatusList: any;
  activitiesWorkUnitList: any;

  row!: ActivityDTO;
  columns!: string[];
  selectedRows = new SelectionModel<ActivityDTO>(false, []);
  tableDataSource = new MatTableDataSource<ActivityDTO>([]);

  ngOnInit(): void {
    this.userRole = this.userService.getUserRole();
    this.userCode = this.userService.getUserCode();
    this.isUserConsultant = this.userService.isUserConsultant();
    this.route.params.subscribe(params => {
      this.taskCode = parseInt(params['id'], 10);
    });
    this.fetchData();
  }

  // Fetches data from the API
  async fetchData(): Promise<void> {
    this.selectedRows.clear();

    try {
      const apiEndpoints = [
        `actividad/tarea/${this.taskCode}`,
        `cuenta/usuario/${this.userCode}`,
        'relaciones/estados_actividad',
        'relaciones/unidades_trabajo_actividad',
      ];

      const [
        activitiesListResponse,
        accountsListResponse,
        activitiesStatusResponse,
        activitiesWorkUnitResponse,
      ] = await this.apiService.getMultiple(apiEndpoints);

      // Processing the activity data response
      if (activitiesListResponse && activitiesListResponse.length > 0) {
        const apiData: ActivityDTO[] = activitiesListResponse;
        // Check if porcentajeProgreso is 100 and update estado accordingly
        apiData.forEach(activity => {
          if (activity.porcentajeProgreso === '100') {
            activity.estado = 'Terminada';
          }
        });
        this.columns = ['seleccion', ...Object.keys(apiData[0])];
        this.tableDataSource.data = apiData;
      } else {
        this.columns = [];
        this.tableDataSource = new MatTableDataSource<ActivityDTO>([]);
      }

      // Processing foreign keys mapping responses
      [
        this.accountsList,
        this.activitiesStatusList,
        this.activitiesWorkUnitList,
      ] = [
        accountsListResponse,
        activitiesStatusResponse,
        activitiesWorkUnitResponse,
      ];

      // Iterate through accountsList
      this.contactsList = [];

      for (const account of this.accountsList) {
        try {
          const contactoResponse = await this.apiService.get(
            `contacto/cuenta/${account.codigo}`
          );

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
        'Fallo al obtener información de API!. Error:' + error
      );
    }
  }

  // Handles row click event
  onRowClick(row: ActivityDTO, event: Event): void {
    event.stopPropagation();
    this.selectedRows.toggle(row);
    this.row = row;
  }

  // Opens dialog with data for creating or updating an activity
  openDialogWithData() {
    const dialogRef = this.dialog.open(FormActivitiesComponent, {
      disableClose: true,
      autoFocus: true,
      width: '80%',
      height: '80%',
      data: {
        taskCode: this.taskCode,
        userCode: this.userCode,
        accountsList: this.accountsList,
        contactsList: this.contactsList,
        activitiesStatusList: this.activitiesStatusList,
        activitiesWorkUnitList: this.activitiesWorkUnitList,
      },
    });
    dialogRef.afterClosed().subscribe(() => this.fetchData());
  }

  // Navigates to the page for creating a new activity
  toCreate() {
    this.openDialogWithData();
  }

  // Updates an existing activity
  toUpdate() {
    const codeContact = this.contactsList.find(
      (u: ContactDTO) =>
        `${u.nombres} ${u.apellidos}` === this.row.contactoAsignado
    ).codigo;
    const codeStatus = this.activitiesStatusList.find(
      (u: RelationshipDTO) => u.nombre === this.row.estado
    ).codigo;
    const codeWorkUnit = this.activitiesWorkUnitList.find(
      (u: RelationshipDTO) => u.nombre === this.row.unidadTrabajo
    ).codigo;

    this.activitiesService.populateForm(
      this.row,
      this.taskCode,
      codeContact,
      codeStatus,
      codeWorkUnit
    );

    this.openDialogWithData();
  }

  // Deletes the selected activity
  async toDelete() {
    await this.activitiesService.delete(this.row.codigo);
    this.fetchData();
  }

  // Navigates back to the tasks page
  goBack() {
    this.router.navigate(['hogar/tareas']);
  }
}
