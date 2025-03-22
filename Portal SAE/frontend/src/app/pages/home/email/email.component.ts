import { EmailDTO } from './../../../interfaces/email/email.dto';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { ApiService } from '../../../services/api.service';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { NotificationService } from '../../../services/notification.service';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormEmailsComponent } from '../../../forms/form-emails/form-emails.component';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { EventDTO } from '../../../interfaces/event/event.dto';

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
  ],
  templateUrl: './email.component.html',
  styleUrl: './email.component.css',
})
export class EmailComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  email!: string;
  password!: string;
  constructor(
    private userService: UserService,
    private apiService: ApiService,
    private notificationService: NotificationService,
    private router: Router,
    public dialog: MatDialog
  ) {
    this.email = "";
    this.password = "";
  }

  userCode!: number | null;
  userRole!: string | null;

  row!: any;
  columns!: string[];
  selectedRows = new SelectionModel<any>(true, []);
  tableDataSource = new MatTableDataSource<EmailDTO>([]);
  eventsList!: EventDTO[];

  selectedEventCode: number | null = null;
  isLoading: boolean = false;
  isApiBusy: boolean = false;

  ngOnInit(): void {
    const emailConf = this.userService.getEmailConf();
    console.log("Datos obtenidos de localStorage:", emailConf)

    if (emailConf) {
      const parsedConfig = JSON.parse(emailConf)
      console.log("Email:", parsedConfig.email);
      console.log("Password:", parsedConfig.password);
      this.email = parsedConfig.email;
      this.password = parsedConfig.password;
      console.log("Email asignado:", this.email)
      console.log("Contraseña asignada:", this.password)
    } else {
      console.log("No se encontró información de email en localStorage.")
    }

    this.userCode = this.userService.getUserCode()
    this.userRole = this.userService.getUserRole()
    this.fetchData()

  }


  // Fetches data from API endpoints, updates table data, and handles errors.
  async fetchData(): Promise<void> {
    this.selectedRows.clear()
    try {
      const apiEndpoint = "email/fetch"
      const requestData = {
        email: this.email,
        password: this.password,
      }

      const emailsResponse = await this.apiService.post(apiEndpoint, requestData)
      console.log("Enviando solicitud con:", requestData)
      console.log("Respuesta del servidor:", emailsResponse)

      if (emailsResponse && emailsResponse.length > 0) {
        const formattedEmails = emailsResponse.map((emailString: string) => {
          const remitenteMatch = emailString.match(/De: ([^<]+)<([^>]+)>/)
          const remitente = remitenteMatch
            ? { nombre: remitenteMatch[1].trim(), email: remitenteMatch[2].trim() }
            : { nombre: "Desconocido", email: "desconocido@email.com" }

          const destinatarioMatch = emailString.match(/Destinatario:\s([^-\n]+)/)
          const destinatario = destinatarioMatch ? destinatarioMatch[1].trim() : "No especificado"
          const asuntoMatch = emailString.match(/Asunto: ([^-]+)/)
          const asunto = asuntoMatch ? asuntoMatch[1].trim() : "Sin asunto"
          const contenidoMatch = emailString.match(/Asunto: [^\n]*\n([\s\S]*?)(?=De:|Fecha de recibido:|$)/)
          const contenido = contenidoMatch ? contenidoMatch[1].trim() : "No hay contenido disponible"
          const fechaMatch = emailString.match(/Fecha de recibido: (\d{4}-\d{2}-\d{2})/)
          const fechaRecibido = fechaMatch ? fechaMatch[1] : "Fecha desconocida"

          return {
            contenido,
            remitente: remitente.nombre + " <" + remitente.email + ">",
            destinatario,
            asunto,
            contenidoCompleto: emailString,
            fechaRecibido,
          }
        })

        console.log("Correos formateados:", formattedEmails)

        this.columns = [ "contenido", "remitente", "destinatario", "asunto", "fechaRecibido"]
        this.tableDataSource.data = formattedEmails
        this.tableDataSource.sort = this.sort
        this.tableDataSource.paginator = this.paginator
        this.eventsList = formattedEmails
      } else {
        console.log("No se encontraron correos")
        this.columns = []
        this.tableDataSource.data = []
        this.eventsList = []
        this.notificationService.warn("No se encontraron correos electrónicos.")
      }
    } catch (error) {
      this.notificationService.warn("Fallo al obtener información de API! Error: " + error)
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
    } catch (error: any) {
      this.notificationService.warn(error.response.data);
    } finally {
      this.isLoading = this.isApiBusy = false;
      this.fetchData();
    }
  }
}
