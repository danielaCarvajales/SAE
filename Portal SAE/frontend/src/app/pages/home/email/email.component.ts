import { Component, input, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { ApiService } from '../../../services/api.service';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { NotificationService } from '../../../services/notification.service';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EmailDTO } from '../../../interfaces/email/email.dto';
import { FormEmailsComponent } from '../../../forms/form-emails/form-emails.component';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { EventDTO } from '../../../interfaces/event/event.dto';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-email',
  standalone: true,
  imports: [
    MatTableModule,
    MatButtonModule,
    MatFormFieldModule,
    MatPaginatorModule,
    MatSortModule,
    MatCheckboxModule,
    MatInputModule,
    MatDialogModule,
    MatSelectModule,
    MatIconModule,
    CommonModule,
    FormsModule
  ],
  templateUrl: './email.component.html',
  styleUrl: './email.component.css',
})
export class EmailComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private userService: UserService,
    private apiService: ApiService,
    private notificationService: NotificationService,
    private router: Router,
    public dialog: MatDialog
  ) { }

  
  userCode!: number | null;
  userRole!: string | null;
  row!: any;
  columns!: string[];
  selectedRows = new SelectionModel<any>(true, []);
  tableDataSource = new MatTableDataSource<EmailDTO>([]);
  eventsList!: EventDTO[];

  searchName: string = '';  
  emails: string[] = [];    
  filteredEmails: string[] = []; 
  isLoading: boolean = false;
  isApiBusy: boolean = false;

  selectedEventCode: number | null = null;


  ngOnInit(): void {
    const emailConf = this.userService.getEmailConf();

    if (!emailConf) {
      this.router.navigate(['hogar/configuracion-email']);
    }

    this.userCode = this.userService.getUserCode();
    this.userRole = this.userService.getUserRole();
    this.fetchData();

    this.tableDataSource.sortingDataAccessor = (data: any, header: string) => {
      if (header === 'fechaRecibido') {
        // Convierte la fecha a un formato comparable (timestamp)
        return new Date(data.fechaRecibido).getTime();
      }
      return data[header];
    };
    this.tableDataSource.sort = this.sort;
  }

   

  // Fetches data from API endpoints, updates table data, and handles errors.
  async fetchData(): Promise<void> {
    this.selectedRows.clear();

    try {
      const apiEndpoints = [
        `email/listar/usuario/${this.userCode}`,
        `evento/usuario/${this.userCode}/${this.userRole}`,
      ];

      const [emailsResponse, eventsResponse] =
        await this.apiService.getMultiple(apiEndpoints);

      if (emailsResponse && emailsResponse.length > 0) {
        this.emails = emailsResponse.map((email: any) => email.emailAddress);  
        this.filteredEmails = [...this.emails]; 
        this.columns = ['seleccion', ...Object.keys(emailsResponse[0])];
        this.tableDataSource.data = emailsResponse;
        this.tableDataSource.sort = this.sort;
        this.sort.active = 'fechaRecibido';
        this.sort.direction = 'desc';

        this.tableDataSource.paginator = this.paginator;
      } else {
        this.columns = [];
        this.tableDataSource.data = [];
      }

      this.eventsList = eventsResponse;
    } catch (error) {
      this.notificationService.warn(
        'Fallo al obtener información de API! Error: ' + error
      );
    }
  }

  // Applies a filter to the table data based on user input.
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.tableDataSource.filter = filterValue.trim().toLowerCase();

    if (this.tableDataSource.paginator) {
      this.tableDataSource.paginator.firstPage();
    }
  }

  // Handles row click event and toggles selection of rows.
  onRowClick(row: EmailDTO, event: Event): void {
    event.stopPropagation();
    const isSelected = this.selectedRows.isSelected(row);
    if (isSelected) {
      this.selectedRows.deselect(row);
    } else {
      this.selectedRows.select(row);
    }
  }

  // Opens a dialog to display email content.
  openDialogWithData(emailContent: string) {
    this.dialog.open(FormEmailsComponent, {
      autoFocus: true,
      width: '80%',
      height: '80%',
      data: {
        content: emailContent,
      },
    });
  }

  // Handles event selection change from a dropdown.
  onEventSelectionChange(event: MatSelectChange): void {
    this.selectedEventCode = event.value;
  }

  // Toggles selection of all rows in the table.
  toggleSelectAll(checked: boolean): void {
    if (checked) {
      this.tableDataSource.data.forEach(row => this.selectedRows.select(row));
    } else {
      this.selectedRows.clear();
    }
  }

  // Checks if all rows in the table are selected.
  areAllRowsSelected(): boolean {
    const numSelected = this.selectedRows.selected.length;
    const numRows = this.tableDataSource.data.length;
    return numSelected === numRows;
  }

  // Edits selected events by updating associated email codes.
  async editEvent(): Promise<void> {
    const emailCodesArray: string[] = [];
    this.selectedRows.selected.forEach((row: EmailDTO) => {
      emailCodesArray.push(row.codigo);
    });
    try {
      if (this.selectedEventCode === null) {
        return;
      }
      this.isLoading = this.isApiBusy = true;
      const response = await this.apiService.put(
        `email/evento/${this.selectedEventCode}`,
        emailCodesArray
      );

      this.notificationService.success(response);
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    } finally {
      this.isLoading = this.isApiBusy = false;
      this.selectedEventCode = null;
      this.fetchData();
    }
  }

  // Updates emails based on user preferences.
  async updateEmails(): Promise<void> {
    const emailConfString = this.userService.getEmailConf();
    const emailConf = emailConfString ? JSON.parse(emailConfString) : null;
    try {
      this.isLoading = this.isApiBusy = true;
      const response = await this.apiService.post('email/capturar', emailConf);
      this.notificationService.success(response);

      const apiEndpoints = [`email/listar/usuario/${this.userCode}`];
      const emailsResponse = await this.apiService.getMultiple(apiEndpoints)

      if (emailsResponse && emailsResponse.length > 0) {
        // Paso 3: Actualizar los datos en la tabla
        this.tableDataSource.data = emailsResponse;
        this.tableDataSource.paginator = this.paginator;
        this.tableDataSource.sort = this.sort;
        this.sort.active = 'fechaRecibido';
        this.sort.direction = 'desc';
      } else {
        this.tableDataSource.data = [];
      }
      
    } catch (error: any) {
      this.notificationService.warn(error.response.data );
    } finally {
      this.isLoading = this.isApiBusy = false;
      this.fetchData();
    }
  }

  

  getNameFromEmail(remitente: string): string {
    const match = remitente.match(/^\[?([^<]*)/);
    return match ? match[1].trim() : remitente;
  }

  getEmailsFromString(input: string): string[] {
    const regex = /[\w._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/g;
    return input.match(regex) || [];
  }
  
  
  

  
}
