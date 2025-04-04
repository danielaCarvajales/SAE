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
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { EventDTO } from '../../../interfaces/event/event.dto';
import { SeeEmailsComponent } from './see-emails/see-emails.component';
import { SendEmailsComponent } from './send-emails/send-emails.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
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
    MatProgressSpinnerModule, 
    FormsModule

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
  filterRemitente: string = '';

  ngOnInit(): void {
    const emailConf = this.userService.getEmailConf();
    if (!emailConf) {
      this.router.navigate(['hogar/configuracion-email']);
    }

    if (emailConf) {
      const parsedConfig = JSON.parse(emailConf)
      this.email = parsedConfig.email;
      this.password = parsedConfig.password;
    } else {
    }

    this.userCode = this.userService.getUserCode()
    this.userRole = this.userService.getUserRole()
    this.fetchData()

  }

  async fetchData(): Promise<void> {
    this.isLoading = true;
    this.selectedRows.clear()
    try {
      const apiEndpoint = "email/fetch"
      const requestData = {
        email: this.email,
        password: this.password,
      }

      const emailsResponse = await this.apiService.post(apiEndpoint, requestData)

      if (emailsResponse && emailsResponse.length > 0) {
        const formattedEmails = emailsResponse.map((emailString: string) => {
          const remitenteMatch = emailString.match(/De: ([^<]+)<([^>]+)>/)
          const remitente = remitenteMatch
            ? { nombre: remitenteMatch[1].trim(), email: remitenteMatch[2].trim() }
            : { nombre: "Desconocido", email: "desconocido@email.com" }

            const destinatarioMatch = emailString.match(/Destinatario:\s([^-\n]+)/)
          let destinatario = "No especificado"

          if (destinatarioMatch && destinatarioMatch[1]) {
            destinatario = destinatarioMatch[1].trim()
          }

          const asuntoMatch = emailString.match(/Asunto: ([^-]+)/)
          const asunto = asuntoMatch ? asuntoMatch[1].trim() : "Sin asunto"
          const contenidoMatch = emailString.match(/Contenido:\s([\s\S]*?)(?=De:|Fecha de recibido:|$)/);
          const contenido = contenidoMatch && contenidoMatch[1].trim() ? contenidoMatch[1].trim() : "-";
          const fechaMatch = emailString.match(/Fecha de recibido: (\d{4}-\d{2}-\d{2})/)
          const fechaRecibido = fechaMatch ? fechaMatch[1] : "Fecha desconocida"

          const adjuntoMatch = emailString.match(/Adjunto:\s([^-\n]+)/)
          const adjunto = adjuntoMatch ? adjuntoMatch[1].trim() : "Sin adjunto"
          const adjuntoIdMatch = emailString.match(/AdjuntoId:\s([a-f0-9-]+)/)
          const adjuntoId = adjuntoIdMatch ? adjuntoIdMatch[1].trim() : null

          return {
            contenido,
            remitente: remitente.nombre + " <" + remitente.email + ">",
            destinatario,
            asunto,
            contenidoCompleto: emailString,
            fechaRecibido,
            adjunto,
            adjuntoId,
          }
        })


        this.columns = ["contenido", "remitente", "destinatario", "asunto", "fechaRecibido"]
        this.tableDataSource.data = formattedEmails
        this.tableDataSource.sort = this.sort
        this.tableDataSource.paginator = this.paginator
        this.eventsList = formattedEmails
      } else {
        this.columns = []
        this.tableDataSource.data = []
        this.eventsList = []
        this.notificationService.warn("No se encontraron correos electrónicos.")
      }
    } catch (error) {
      this.notificationService.warn("Fallo al obtener información de API! Error: " + error)
    }finally {
      this.isLoading = false;  
    }
  }


  onRowClick(row: EmailDTO, event: Event): void {
    event.stopPropagation();
    const isSelected = this.selectedRows.isSelected(row);
    if (isSelected) {
      this.selectedRows.deselect(row);
    } else {
      this.selectedRows.select(row);
    }
  }


  openEmailDetails(emailData: any) {
    const dialogRef = this.dialog.open(SeeEmailsComponent, {
      width: '600px',
      maxHeight: '60vh',
      hasBackdrop: false,
      panelClass: 'transparent-dialog',
      data: emailData
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  openEmailForm(): void {
    const dialogRef = this.dialog.open(SendEmailsComponent, {
      width: '600px',
      maxHeight: '60vh',
      position: { bottom: '75px', right: '40px' },
      hasBackdrop: true,
      panelClass: 'custom-dialog-container',
      backdropClass: 'transparent-backdrop', 
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  formatEmailContent(content: string): string {
    let formatted = content.replace(/\n/g, '<br>');
    formatted = formatted.replace(
      /(https?:\/\/[^\s]+)/g,
      '<a href="$1" target="_blank">$1</a>'
    );

    return formatted;
  }

  formatDestinatarios(destinatarios: string): string {
    if (!destinatarios || destinatarios === 'No especificado') {
      return 'No especificado';
    }
    let cleanDestinatarios = destinatarios.replace(/^\[|\]$/g, '');
    
    const listaDestinatarios = cleanDestinatarios.split(/,\s*/)
      .map(d => d.trim())
      .filter(d => d.length > 0);
  
    if (listaDestinatarios.length === 0) {
      return 'No especificado';
    } else if (listaDestinatarios.length === 1) {
      return listaDestinatarios[0];
    } else {
      return `${listaDestinatarios[0]} +${listaDestinatarios.length - 1}`;
    }
  }


  applyRemitenteFilter() {
    this.tableDataSource.filterPredicate = (data: EmailDTO, filter: string) => {
      return data.remitente.toLowerCase().includes(filter.toLowerCase());
    };
    
    this.tableDataSource.filter = this.filterRemitente.trim();
    
    if (this.tableDataSource.paginator) {
      this.tableDataSource.paginator.firstPage();
    }
  }
  

  clearRemitenteFilter() {
    this.filterRemitente = '';
    this.tableDataSource.filter = '';
  }
  
  
}
