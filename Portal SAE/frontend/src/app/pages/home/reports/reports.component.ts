import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatRippleModule } from '@angular/material/core';
import { MatIcon } from '@angular/material/icon';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [
    MatCardModule,
    MatIcon,
    CommonModule,
    MatButtonModule,
    MatRippleModule,
  ],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css',
})
export class ReportsComponent {
  constructor(private router: Router) {}

  CARD_DATA = [
    { title: 'TAREAS', icon: 'task', url: 'hogar/informes/tareas' },
    { title: 'EVENTOS', icon: 'event', url: 'hogar/informes/eventos' },
    {
      title: 'ACTIVIDADES',
      icon: 'task_alt',
      url: 'hogar/informes/actividades',
    },
    {
      title: 'INCIDENCIAS',
      icon: 'local_activity',
      url: 'hogar/informes/incidencias',
    },
  ];

  redirectToPage(route: string): void {
    this.router.navigate([route]);
  }
}
