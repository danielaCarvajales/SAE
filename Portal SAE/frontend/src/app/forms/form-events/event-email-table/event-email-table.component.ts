import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ApiService } from '../../../services/api.service';
import { NotificationService } from '../../../services/notification.service';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { EmailDTO } from '../../../interfaces/email/email.dto';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormEmailsComponent } from '../../form-emails/form-emails.component';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-event-email-table',
  standalone: true,
  imports: [
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatIconModule,
    MatDialogModule,
    MatInputModule,
    CommonModule,
  ],
  templateUrl: './event-email-table.component.html',
  styleUrl: './event-email-table.component.css',
})
export class EventEmailTableComponent implements OnInit {
  @Input() eventCode!: number | null; // Input property to receive the event code from the parent component.
  @Input() isEmail!: boolean; // Input property to indicate if emails were selected in the form.

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  row!: any;
  columns!: string[];
  tableDataSource = new MatTableDataSource<EmailDTO>([]);

  constructor(
    private apiService: ApiService,
    private notificationService: NotificationService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.fetchData();
  }

  // Function to fetch email data related to the event from the API
  async fetchData(): Promise<void> {
    try {
      const response = await this.apiService.get(
        `email/listar/evento/${this.eventCode}`
      );
      if (response && response.length > 0) {
        this.columns = Object.keys(response[0]);
        this.tableDataSource.data = response;
        this.tableDataSource.sort = this.sort;
        this.tableDataSource.paginator = this.paginator;
      } else {
        this.columns = [];
        this.tableDataSource.data = [];
      }
    } catch (error) {
      this.notificationService.warn(
        'Fallo al obtener información de API! Error: ' + error
      );
    }
  }

  // Function to apply filter to table data source based on user input
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.tableDataSource.filter = filterValue.trim().toLowerCase();

    if (this.tableDataSource.paginator) {
      this.tableDataSource.paginator.firstPage();
    }
  }

  // Function to open dialog window and display content of a specific email
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
}
