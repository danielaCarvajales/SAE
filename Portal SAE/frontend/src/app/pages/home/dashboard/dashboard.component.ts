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

  private colors: string[];
  private actividades: any[]
  private userCode!: number | null;
  private userRole!: string | null;

  public calendarData: { [key: string]: any[] };

  public activityData: any[];

  public currentView: 'day' | 'week' | 'month' = 'week';


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
    this.calendarData = {};
    this.activityData = [];
  }

  ngOnInit(): void {
    const savedState = localStorage.getItem('isChecked');
    this.isChecked = savedState === 'true';
    this.generateWeekDays(this.selectedDate);
    this.userCode = this.userService.getUserCode();
    this.userRole = this.userService.getUserRole();
    this.loadingActivity('evento');
    console.log('calendarData:', this.calendarData);
    console.log('daysOfWeek:', this.daysOfWeek);
    console.log('hours:', this.hours);
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
    return Array.from({ length: 24 }, (_, i) => `${(i % 12 || 12).toString().padStart(2, '0')}${i < 12 ? ' am' : ' pm'}`);
  }

  togglerCheck() {
    this.isChecked = !this.isChecked;
    localStorage.setItem('isChecked', this.isChecked.toString());
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
        console.log('Datos recibidos:', data);
        this.activityData = data;
        this.updateCalendar();
        this.cdr.detectChanges(); // Forzar la detección de cambios
      },
      (error) => {
        console.error(`Error al obtener ${tipo}:`, error);
      }
    );
  }

  updateCalendar() {
    console.log('Actividades cargadas:', this.activityData);

    try {
      this.calendarData = this.activityData.reduce((acc, actividad) => {
        const fechaActividad = actividad.fechaHoraInicio;

        if (!fechaActividad) {
          console.warn('Actividad sin fecha válida:', actividad);
          return acc;
        }
        const dateKey = formatDate(fechaActividad, 'dd/MM/yyyy', 'en-US');
        const hora = new Date(fechaActividad).getHours();
        actividad.hora = `${hora % 12 === 0 ? 12 : hora % 12}${hora < 12 ? ' am' : ' pm'}`;
        console.log('Actividad:', actividad.asunto, 'Hora:', actividad.hora);
        if (!acc[dateKey]) acc[dateKey] = [];
        acc[dateKey].push(actividad);
        return acc;
      }, {} as { [key: string]: any[] });

    } catch (error) {
      console.error('Error al actualizar el calendario:', error);
    }
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
}
