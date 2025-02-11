import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { TaskDTO } from '../../../../interfaces/task/task.dto';
import { ApiService } from '../../../../services/api.service';
import { NotificationService } from '../../../../services/notification.service';
import { UserService } from '../../../../services/user.service';

@Component({
  selector: 'app-report-tasks',
  standalone: true,
  imports: [
    MatTableModule,
    MatExpansionModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSortModule,
    DatePipe,
  ],
  templateUrl: './report-tasks.component.html',
  styleUrl: './report-tasks.component.css',
})
export class ReportTasksComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private apiService: ApiService,
    private notificationService: NotificationService,
    private userService: UserService
  ) {}

  userCode!: number | null;
  userRole!: string | null;

  columns!: string[];
  tableDataSource = new MatTableDataSource<TaskDTO>([]);

  ngOnInit(): void {
    this.userCode = this.userService.getUserCode();
    this.userRole = this.userService.getUserRole();
    this.fetchData();
  }

  // Method to fetch data from the API
  async fetchData() {
    try {
      const response = await this.apiService.get(
        `tarea/usuario/${this.userCode}/${this.userRole}`
      );

      if (response && response.length > 0) {
        const apiData = response;
        this.columns = Object.keys(apiData[0]);
        this.tableDataSource.data = apiData;
        this.tableDataSource.sort = this.sort;
        this.tableDataSource.paginator = this.paginator;
      } else {
        this.columns = [];
        this.tableDataSource.data = [];
      }
    } catch (error) {
      this.notificationService.warn(
        'Fallo al obtener información de la API! Error: ' + error
      );
    }
  }

  // Method to apply filter to table data
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.tableDataSource.filter = filterValue.trim().toLowerCase();

    if (this.tableDataSource.paginator) {
      this.tableDataSource.paginator.firstPage();
    }
  }

  // Method to parse date string to Date object
  parseDate(dateString: string): Date {
    return new Date(dateString);
  }
}
