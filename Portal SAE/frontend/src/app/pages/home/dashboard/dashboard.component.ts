import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Importa FormsModule para usar ngModel
import { BsDatepickerModule, BsDatepickerConfig, BsLocaleService } from 'ngx-bootstrap/datepicker';
import { esLocale } from 'ngx-bootstrap/locale';
import { defineLocale } from 'ngx-bootstrap/chronos';
import { SelectionModel } from '@angular/cdk/collections';
import { ActivityDTO } from '../../../interfaces/activity/activity.dto';
import { ApiService } from '../../../services/api.service';
import { MatTableDataSource } from '@angular/material/table';
import { IncidentDTO } from '../../../interfaces/incident/incident.dto';
import { UserService } from '../../../services/user.service';
import { EventDTO } from '../../../interfaces/event/event.dto';
import { TaskDTO } from '../../../interfaces/task/task.dto';

const customEsLocale = {
  ...esLocale,
  weekdaysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vie', 'Sab'],
};
defineLocale('es', customEsLocale);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterModule,
    CommonModule,
    FormsModule,
    BsDatepickerModule,
    NgClass,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {

  public isChecked: boolean;
  public currentWeek: string;
  public daysOfWeeks: { [key: string]: string } = {};
  public hours: string[];
  public daysOfWeek: { name: string; date: string }[] = [];
  public selectedDate: Date;
  public showDatepicker: boolean;
  public datepickerConfig: Partial<BsDatepickerConfig>;
  public columns!: string[];
  public selectedRows = new SelectionModel<ActivityDTO>(false, []);
  public tableDataSource = new MatTableDataSource<ActivityDTO>([]);

  private colors : string[];
  private actividades : any[];
  private userCode!: number | null;
  private userRole!: string | null;

  public calendarData: { [key: string]: any[] } ;

  row!: EventDTO;
  rowTwo!: IncidentDTO;
  rowThere!: TaskDTO;

  constructor(private apiService: ApiService, private localeService: BsLocaleService,   private userService: UserService) {
    this.isChecked = false;
    this.selectedDate = new Date();
    this.currentWeek = '';
    this.calculateCurrentWeek(this.selectedDate);
    this.hours = this.getDayHours();
    this.showDatepicker = false;
    this.localeService.use('es-custom');
    this.datepickerConfig = Object.assign({}, {
      containerClass: 'theme-default',
      isAnimated: true,
      dateInputFormat: 'DD/MM/YYYY',
      locale: 'es-custom',
      showWeekNumbers: false
    });
    this.colors = [
      '#0d2296', 
      '#3d81f4', 
      '#be8123', 
      '#99211f', 
      '#525253', 
      '#868c6a', 
      '#f70293', 
      '#198119', 
    ];
    this.actividades = [];
    this.calendarData ={};
  }

  ngOnInit(): void {
    const savedState = localStorage.getItem('isChecked');
    this.isChecked = savedState === 'true';
    this.generateWeekDays(this.selectedDate);
    this.userCode = this.userService.getUserCode();
    this.userRole = this.userService.getUserRole();
    this.loadingActivity('evento');
    
    
  }

 
  generateWeekDays(referenceDate?: Date) {
    const today = referenceDate || new Date();
    const startOfWeek = new Date(today);
    startOfWeek.setDate(today.getDate() - today.getDay());
    const weekDays = ['Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'];

    this.daysOfWeek = weekDays.map((day, index) => {
      const date = new Date(startOfWeek);
      date.setDate(startOfWeek.getDate() + index);
      return {
        name: day,
        date: `${date.getDate()}/${date.getMonth() + 1}`
      };
    });
  }

  /* Horas del día */
  getDayHours(): string[] {
    const hours: string[] = [];
    for (let i = 1; i <= 24; i++) {
      const period = i < 12 ? 'am' : 'pm';
      const hour = i % 12 === 0 ? 12 : i % 12;
      hours.push(`${hour}${period}`);
    }
    return hours;
  }

  togglerCheck() {
    this.isChecked = !this.isChecked;
    localStorage.setItem('isChecked', this.isChecked.toString());
  }
  /* Calculo de la semana  */
  calculateCurrentWeek(referenceDate?: Date): void {
    const today = referenceDate || new Date();
    const firstDayOfWeek = new Date(today);
    firstDayOfWeek.setDate(today.getDate() - today.getDay() );
    const lastDayOfWeek = new Date(firstDayOfWeek);
    lastDayOfWeek.setDate(firstDayOfWeek.getDate() + 6);

    const formatDate = (date: Date) =>
      date.toLocaleDateString('es-Es', { month: 'short', day: '2-digit' }).replace(',', '');
    this.currentWeek = `${formatDate(firstDayOfWeek)} - ${formatDate(lastDayOfWeek)}, ${today.getFullYear()}`;
  }

  onDateSelect(date: Date) {
    if (date) {
      this.selectedDate = date;
      this.generateWeekDays(date);
      this.calculateCurrentWeek(date);
    }
  }

  navigateWeek(direction: number): void {
    const newDate = new Date(this.selectedDate);
    newDate.setDate(newDate.getDate() + direction * 7); 
    
    this.selectedDate = newDate;
    this.generateWeekDays(newDate);
    this.calculateCurrentWeek(newDate);
  }
  
  toggleDatepicker() {
    this.showDatepicker = !this.showDatepicker;
  }

   getColorByIndex(index: number): string {
    return this.colors[index % this.colors.length];
  }
  
  loadingActivity(tipo: string) {
    let url = `${tipo}/usuario/${this.userCode}/${this.userRole}`;

    this.apiService.get(url).then(
      (data) => {
        this.actividades = data;
        this.updateCalendar();
      },
      (error) => {
        console.error(`Error al obtener ${tipo}:`, error);
      }
    );
  }

  updateCalendar() {
    this.calendarData = {}; // Reiniciar datos
    this.actividades.forEach((actividad) => {
      const fecha = actividad.fecha; 
      const hora = actividad.hora; 
      const key = `${fecha}-${hora}`;
  
      if (!this.calendarData[key]) {
        this.calendarData[key] = [];
      }
  
      this.calendarData[key].push({
        tipo: actividad.tipo,
        nombre: 
        actividad.tipo === 'tarea' || actividad.tipo === 'evento' ? actividad.asunto : actividad.pregunta,
        color: this.getColorByIndex(this.actividades.indexOf(actividad)),
      });
    });
  
    console.log('Datos organizados en el calendario:', this.calendarData);
  }

 

}
