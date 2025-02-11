import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { UserByRoleDTO } from '../../../interfaces/user/userByRole.dto';
import { ApiService } from '../../../services/api.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-consultants',
  standalone: true,
  imports: [
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    CommonModule,
  ],
  templateUrl: './consultants.component.html',
  styleUrl: './consultants.component.css',
})
export class ConsultantsComponent implements OnInit {
  constructor(
    private apiService: ApiService,
    private notificationService: NotificationService
  ) {}

  columns!: string[];
  tableDataSource = new MatTableDataSource<UserByRoleDTO>([]);

  ngOnInit(): void {
    this.fetchData();
  }

  // Function to fetch data from API
  async fetchData(): Promise<void> {
    try {
      const response = await this.apiService.get(`usuario/consultor`);
      if (response && response.length > 0) {
        const apiData = response;
        this.columns = Object.keys(apiData[0]);
        this.tableDataSource.data = apiData;
      } else {
        this.columns = [];
        this.tableDataSource = new MatTableDataSource<UserByRoleDTO>([]);
      }
    } catch (error) {
      this.notificationService.warn(
        'Fallo al obtener información de API! Error: ' + error
      );
    }
  }
}
