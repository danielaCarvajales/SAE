import { SelectionModel } from '@angular/cdk/collections';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../environments/environment';
import { ContactsService } from '../../../../forms/form-contacts/contacts.service';
import { FormContactsComponent } from '../../../../forms/form-contacts/form-contacts.component';
import { ContactDTO } from '../../../../interfaces/contact/contact.dto';
import { ApiService } from '../../../../services/api.service';
import { NotificationService } from '../../../../services/notification.service';
import { UserService } from '../../../../services/user.service';
import { RelationshipDTO } from '../../../../interfaces/relationship.dto';

@Component({
  selector: 'app-contacts',
  standalone: true,
  imports: [
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    CommonModule,
    FormContactsComponent,
  ],
  templateUrl: './contacts.component.html',
  styleUrl: './contacts.component.css',
})
export class ContactsComponent implements OnInit {
  constructor(
    private contactsService: ContactsService,
    private apiService: ApiService,
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute,
    private notificationService: NotificationService,
    public dialog: MatDialog
  ) {}

  API_URL!: string;
  userCode!: number | null;
  accountCode!: number;
  identityTypesList: any;

  row!: ContactDTO;
  columns!: string[];
  selectedRows = new SelectionModel<ContactDTO>(false, []);
  tableDataSource = new MatTableDataSource<ContactDTO>([]);

  ngOnInit(): void {
    this.userCode = this.userService.getUserCode();
    this.API_URL = environment.API_URL;
    this.route.params.subscribe(params => {
      this.accountCode = parseInt(params['id'], 10);
    });
    this.fetchData();
  }

  // Function to fetch data from API
  async fetchData(): Promise<void> {
    this.selectedRows.clear();

    try {
      const apiEndpoints = [
        `contacto/cuenta/${this.accountCode}`,
        'relaciones/tipos_identificacion',
      ];

      const [contactsListResponse, identityTypesResponse] =
        await this.apiService.getMultiple(apiEndpoints);

      if (contactsListResponse && contactsListResponse.length > 0) {
        const apiData: ContactDTO[] = contactsListResponse;
        this.columns = ['seleccion', ...Object.keys(apiData[0])];
        this.tableDataSource.data = apiData;
      } else {
        this.columns = [];
        this.tableDataSource = new MatTableDataSource<ContactDTO>([]);
      }

      this.identityTypesList = identityTypesResponse;
    } catch (error) {
      this.notificationService.warn(
        'Fallo al obtener información de API! Error: ' + error
      );
    }
  }

  // Function called when a row is clicked
  onRowClick(row: ContactDTO, event: Event): void {
    event.stopPropagation();
    this.selectedRows.toggle(row);
    this.row = row;
  }

  // Function to open dialog window for creating a new contact
  openDialogWithData() {
    const dialogRef = this.dialog.open(FormContactsComponent, {
      disableClose: true,
      autoFocus: true,
      width: '80%',
      height: '80%',
      data: {
        userCode: this.userCode,
        accountCode: this.accountCode,
        identityTypesList: this.identityTypesList,
      },
    });

    dialogRef.afterClosed().subscribe(() => this.fetchData());
  }

  // Function to navigate to the form for creating a new contact
  toCreate() {
    this.openDialogWithData();
  }

  // Function to navigate to the form for updating an existing contact
  toUpdate() {
    const codeIdentityType = this.identityTypesList.find(
      (u: RelationshipDTO) => u.nombre === this.row.tipoIdentificacion
    ).codigo;
    this.contactsService.populateForm(this.row, codeIdentityType);
    this.openDialogWithData();
  }

  // Function to delete a contact
  async toDelete() {
    await this.contactsService.delete(this.row.codigo);
    this.fetchData();
  }

  // Function to navigate back to the accounts page
  goBack() {
    this.router.navigate(['hogar/cuentas']);
  }
}
