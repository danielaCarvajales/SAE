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
import { Router } from '@angular/router';
import { FormTasksComponent } from '../../../forms/form-tasks/form-tasks.component';
import { TasksService } from '../../../forms/form-tasks/tasks.service';
import { TaskDTO } from '../../../interfaces/task/task.dto';
import { ApiService } from '../../../services/api.service';
import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { UserByRoleDTO } from '../../../interfaces/user/userByRole.dto';
import { AccountDTO } from '../../../interfaces/account/account.dto';
import { RelationshipDTO } from '../../../interfaces/relationship.dto';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    CommonModule,
    FormTasksComponent,
    MatExpansionModule,
  ],
  templateUrl: './tasks.component.html',
  styleUrl: './tasks.component.css',
})
export class TasksComponent implements OnInit {
  constructor(
    private tasksService: TasksService,
    private userService: UserService,
    private notificationService: NotificationService,
    private apiService: ApiService,
    private router: Router,
    public dialog: MatDialog
  ) {}

  userCode!: number | null;
  userRole!: string | null;
  isUserConsultant!: boolean;
  accountsList: any;
  consultantsList: any;
  businessmenList: any;
  taskStatusList: any;
  taskTypesList: any;
  codeConsultant: any;

  row!: TaskDTO;
  columns!: string[];
  selectedRows = new SelectionModel<TaskDTO>(false, []);
  tableDataSource = new MatTableDataSource<TaskDTO>([]);

  ngOnInit(): void {
    this.userCode = this.userService.getUserCode();
    this.userRole = this.userService.getUserRole();
    this.isUserConsultant = this.userService.isUserConsultant();
    this.fetchData();
  }

  // Fetches data from the API
  async fetchData(): Promise<void> {
    this.selectedRows.clear();

    try {
      const apiEndpoints = [
        `tarea/usuario/${this.userCode}/${this.userRole}`,
        'usuario/consultor',
        'usuario/empresario',
        'relaciones/estados_tarea',
        'relaciones/tipos_tarea',
        `cuenta/usuario/${this.userCode}`,
      ];

      const [
        tasksListResponse,
        consultantsListResponse,
        businessmenListResponse,
        tasksStatusResponse,
        tasksTypeResponse,
        accountListResponse,
      ] = await this.apiService.getMultiple(apiEndpoints);

      // Processing the task data response
      if (tasksListResponse && tasksListResponse.length > 0) {
        const apiData: TaskDTO[] = tasksListResponse;

        // Check if porcentajeProgreso is 100 and update estado accordingly
        apiData.forEach(task => {
          if (task.porcentajeProgreso === '100') {
            task.estado = 'Terminada';
          } else if (task.porcentajeProgreso === '0') {
            task.estado = 'No iniciada';
          }
        });

        this.columns = ['seleccion', ...Object.keys(apiData[0])];
        this.tableDataSource.data = apiData;
      } else {
        this.columns = [];
        this.tableDataSource = new MatTableDataSource<TaskDTO>([]);
      }

      // Processing foreign keys mapping responses
      [
        this.consultantsList,
        this.businessmenList,
        this.taskStatusList,
        this.taskTypesList,
        this.accountsList,
      ] = [
        consultantsListResponse,
        businessmenListResponse,
        tasksStatusResponse,
        tasksTypeResponse,
        accountListResponse,
      ];
    } catch (error) {
      this.notificationService.warn(
        'Fallo al obtener información de API! Error: ' + error
      );
    }
  }

  // Handles row click event
  onRowClick(row: TaskDTO, event: Event): void {
    event.stopPropagation();
    this.selectedRows.toggle(row);
    this.row = row;
  }

  // Opens dialog with data for creating or updating a task
  openDialogWithData() {
    const dialogRef = this.dialog.open(FormTasksComponent, {
      disableClose: true,
      autoFocus: true,
      width: '80%',
      height: '80%',
      data: {
        userCode: this.userCode,
        userRole: this.userRole,
        accountsList: this.accountsList,
        consultantsList: this.consultantsList,
        businessmenList: this.businessmenList,
        taskStatusList: this.taskStatusList,
        taskTypesList: this.taskTypesList,
      },
    });
    dialogRef.afterClosed().subscribe(() => this.fetchData());
  }

  // Navigates to the page for creating a new task
  toCreate() {
    this.openDialogWithData();
  }

  // Updates an existing task
  async toUpdate(): Promise<any> {
    try {
      this.codeConsultant = this.consultantsList.find(
        (u: UserByRoleDTO) => u.nombre === this.row.consultorAsignado
      ).codigo;

      const response = await this.apiService.get(
        `cuenta/usuario/${this.codeConsultant}`
      );

      this.accountsList = response;
      const codeAccount = this.accountsList.find(
        (u: AccountDTO) => u.paginaWeb === this.row.cuentaAsignada
      ).codigo;

      const codeBusinessman = this.businessmenList.find(
        (u: UserByRoleDTO) => u.nombre === this.row.empresarioAsignado
      ).codigo;

      const codeStatus = this.taskStatusList.find(
        (u: RelationshipDTO) => u.nombre === this.row.estado
      ).codigo;

      const codeType = this.taskTypesList.find(
        (u: RelationshipDTO) => u.nombre === this.row.tipo
      ).codigo;

      this.tasksService.populateForm(
        this.row,
        codeAccount,
        this.codeConsultant,
        codeBusinessman,
        codeStatus,
        codeType
      );

      this.openDialogWithData();
    } catch (error) {
      this.notificationService.warn(
        'Error al actualizar la tarea. Error: ' + error
      );
    }
  }

  // Deletes a task
  async toDelete() {
    await this.tasksService.delete(this.row.codigo);
    this.fetchData();
  }

  // Navigates to the page for viewing activities related to the selected task
  toActivities() {
    this.router.navigate([`hogar/tareas/actividades/${this.row.codigo}`]);
  }
}
