import { CommonModule, formatDate } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Importa FormsModule para usar ngModel
import { BsDatepickerModule, BsDatepickerConfig, BsLocaleService } from 'ngx-bootstrap/datepicker';
import { esLocale } from 'ngx-bootstrap/locale';
import { defineLocale, isFirstDayOfWeek } from 'ngx-bootstrap/chronos';
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
defineLocale('es-custom', customEsLocale);

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
  public filteredActivityData: any[];

  private colors: string[];
  private actividades: any[]
  private userCode!: number | null;
  private userRole!: string | null;

  public calendarData: { [key: string]: any[] };
  public activityData: any[];
  public currentView: 'day' | 'week' | 'month' = 'week';


  public selectedType: string | null;
  public availableStatuses: string[];

  constructor(private apiService: ApiService, private localeService: BsLocaleService,
    private userService: UserService, private cdr: ChangeDetectorRef) {
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
      'rgb(8, 21, 82)', 
      'rgb(33, 97, 150)', /* */
          'rgb(53, 107, 75)',
      
     
      

    ];
    this.filteredActivityData = [];
    this.actividades = [];
    this.calendarData = {};
    this.activityData = [];
    this.selectedType = null;
    this.availableStatuses = [];
  }

  ngOnInit(): void {
    const savedState = localStorage.getItem('isChecked');
    this.isChecked = savedState === 'true';
    this.generateWeekDays(this.selectedDate);
    this.userCode = this.userService.getUserCode();
    this.userRole = this.userService.getUserRole();
   /*  this.loadingActivity('evento');
    console.log('calendarData:', this.calendarData);
    console.log('daysOfWeek:', this.daysOfWeek);
    console.log('hours:', this.hours); */
  }


  generateWeekDays(referenceDate?: Date) {
    const today = referenceDate || new Date();
    const startOfWeek = new Date(today);
    startOfWeek.setDate(today.getDate() - today.getDay());
    const weekDays = ['Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'];
    this.daysOfWeek = weekDays.map((day, index) => {
      const date = new Date(startOfWeek);
      date.setDate(startOfWeek.getDate() + index);
      const formattedDate = formatDate(date, 'dd/MM/yyyy', 'en-US');
      return {
        name: day,
        date: formattedDate
      };
    });
  }

  getDayHours(): string[] {
    return Array.from({ length: 24 }, (_, i) => {
      const hour = i % 12 || 12;
      const period = i < 12 ? 'AM' : 'PM';
      return `${hour.toString().padStart(2, '0')} ${period}`;
    });
  }
  
 
  calculateCurrentWeek(referenceDate?: Date): void {
    const today = referenceDate || new Date();
    const firstDayOfWeek = new Date(today);
    firstDayOfWeek.setDate(today.getDate() - today.getDay());
    const lastDayOfWeek = new Date(firstDayOfWeek);
    lastDayOfWeek.setDate(firstDayOfWeek.getDate() + 6);

    const formatDate = (date: Date) =>
      date.toLocaleDateString('es-Es', { month: 'short', day: '2-digit' }).replace(',', '');
    this.currentWeek = `${formatDate(firstDayOfWeek)} - ${formatDate(lastDayOfWeek)}, ${today.getFullYear()}`;
  }

  onDateSelect(date: Date) {
    if (date) {
      this.selectedDate = date;
      if (this.currentView === 'day') {
        this.generateDayView(date);
      } else if (this.currentView === 'week') {
        this.generateWeekDays(date);
      } else if (this.currentView === 'month') {
        this.generateMonthView(date);
      }
      this.calculateCurrentWeek(date);
    }
  }

  navigateWeek(direction: number): void {
    this.selectedDate.setDate(this.selectedDate.getDate() + direction * 7);
    this.generateWeekDays(this.selectedDate);
    this.calculateCurrentWeek(this.selectedDate);
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
        this.activityData = data;
        this.filteredActivityData = [...data]; 
        this.updateCalendar();
        this.cdr.detectChanges();
      },
      (error) => {
        console.error(`Error al obtener ${tipo}:`, error);
      }
    );
  
    this.selectedType = tipo;
    switch (tipo) {
      case 'tarea':
        this.availableStatuses = ['No iniciada', 'En progreso', 'Terminada', 'Planeada'];
        break;
      case 'evento':
        this.availableStatuses = ['Planeado', 'Realizado', 'Pendiente', 'Cancelado'];
        break;
      case 'incidencia':
        this.availableStatuses = ['Revisada', 'En curso', 'Resuelta'];
        break;
    }
  }
  
  applyFilter(estado: string) {
    this.filteredActivityData = this.activityData.filter(actividad => 
      actividad.estado.trim().toLowerCase() === estado.trim().toLowerCase());
      console.log('Estados en la base de datos:', this.activityData.map(a => a.estado));

    console.log('Estado seleccionado:', estado, 'Filtradas:', this.filteredActivityData);

    this.updateCalendar();
  }
  
  updateCalendar() {
    this.calendarData = {};
  
    this.filteredActivityData.forEach(actividad => {
      if (!actividad.fechaHoraInicio) return;
  
      const fechaActividad = new Date(actividad.fechaHoraInicio);
      const dateKey = formatDate(fechaActividad, 'dd/MM/yyyy', 'en-US');
      const hora24 = fechaActividad.getHours();
      const hora12 = hora24 % 12 || 12;
      const period = hora24 < 12 ? 'AM' : 'PM';
      actividad.hora = `${hora12.toString().padStart(2, '0')} ${period}`;
  
      if (!this.calendarData[dateKey]) {
        this.calendarData[dateKey] = [];
      }
      this.calendarData[dateKey].push(actividad);
    });
  }
  
  isActivityVisible(activity: any, dateKey: string): boolean {
    const activityDate = new Date(activity.fechaHoraInicio);
    const selectedDate = new Date(this.selectedDate);
  
    if (this.currentView === 'day') {
      return formatDate(activityDate, 'dd/MM/yyyy', 'en-US') === formatDate(selectedDate, 'dd/MM/yyyy', 'en-US');
    } else if (this.currentView === 'week') {
      return true;
    } else if (this.currentView === 'month') {
      return activityDate.getMonth() === selectedDate.getMonth() && activityDate.getFullYear() === selectedDate.getFullYear();
    }
    return false;
  }

  switchToDayView() {
    this.currentView = 'day';
    this.generateDayView(this.selectedDate);
  }

  switchToWeekView() {
    this.currentView = 'week';
    this.generateWeekDays(this.selectedDate);
  }

  switchToMonthView() {
    this.currentView = 'month';
    this.generateMonthView(this.selectedDate);
  }

  generateDayView(referenceDate?: Date) {
    const today = referenceDate || new Date();
    const formattedDate = formatDate(today, 'dd/MM/yyyy', 'en-US');
    this.daysOfWeek = [{
      name: formatDate(today, 'EEE', 'en-US'),
      date: formattedDate
    }];
  }

  generateMonthView(referenceDate?: Date) {
    const today = referenceDate || new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const endOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);

    this.daysOfWeek = [];
    for (let day = startOfMonth; day <= endOfMonth; day.setDate(day.getDate() + 1)) {
      const formattedDate = formatDate(day, 'dd/MM/yyyy', 'en-US');
      this.daysOfWeek.push({
        name: formatDate(day, 'EEE', 'en-US'),
        date: formattedDate
      });
    }
  }

  navigate(direction: number): void {
    if (this.currentView === 'day') {
      this.selectedDate.setDate(this.selectedDate.getDate() + direction);
      this.generateDayView(this.selectedDate);
    } else if (this.currentView === 'week') {
      this.selectedDate.setDate(this.selectedDate.getDate() + direction * 7);
      this.generateWeekDays(this.selectedDate);
    } else if (this.currentView === 'month') {
      this.selectedDate.setMonth(this.selectedDate.getMonth() + direction);
      this.generateMonthView(this.selectedDate);
    }
    this.calculateCurrentWeek(this.selectedDate);
  }

  goToToday(): void {
    this.selectedDate = new Date();
    if (this.currentView === 'day') {
      this.generateDayView(this.selectedDate);
    } else if (this.currentView === 'week') {
      this.generateWeekDays(this.selectedDate);
    } else if (this.currentView === 'month') {
      this.generateMonthView(this.selectedDate);
    }
    this.calculateCurrentWeek(this.selectedDate);
  }

  getColorByType(): string {
    switch (this.selectedType) {
      case 'incidencia':
        return this.colors[0]; 
      case 'evento':
        return this.colors[1]; 
      case 'tarea':
        return this.colors[2]; 
      default:
        return this.colors[3]; 
    }
  }
  

}
