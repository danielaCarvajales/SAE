import { CommonModule } from '@angular/common';
import {
  Component,
  OnInit,
  Inject,
  ViewChild,
  AfterViewInit,
} from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelect, MatSelectModule } from '@angular/material/select';
import { ApiService } from '../../services/api.service';
import { EventsService } from './events.service';
import { UserService } from '../../services/user.service';
import { EventDTO } from '../../interfaces/event/event.dto';
import { EventEmailTableComponent } from './event-email-table/event-email-table.component';

@Component({
  selector: 'app-form-events',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    EventEmailTableComponent,
  ],
  templateUrl: './form-events.component.html',
  styleUrl: './form-events.component.css',
})
export class FormEventsComponent implements OnInit, AfterViewInit {
  @ViewChild('selectEventType') selectEventType!: MatSelect;

  userCode!: number | null;
  userRole!: string | null;
  isUserBusinessman!: boolean;
  isUserConsultant!: boolean;
  consultantsList: any;
  businessmenList: any;
  eventsTypesList: any;
  eventsStatusList: any;
  eventsVisibilitiesList: any;
  eventsNotifyByList: any;
  accountsList: any;
  contactsList: any = [];

  eventTypeIsWhatsapp: boolean = false;
  eventTypeIsEmail: boolean = false;

  constructor(
    public eventsService: EventsService,
    private userService: UserService,
    public apiService: ApiService,
    public dialogRef: MatDialogRef<FormEventsComponent>,
    @Inject(MAT_DIALOG_DATA) public dialogData: any
  ) {
    this.userCode = dialogData.userCode;
    this.userRole = dialogData.userRole;
    this.consultantsList = dialogData.consultantsList;
    this.businessmenList = dialogData.businessmenList;
    this.eventsTypesList = dialogData.eventsTypesList;
    this.eventsStatusList = dialogData.eventsStatusList;
    this.eventsVisibilitiesList = dialogData.eventsVisibilitiesList;
    this.eventsNotifyByList = dialogData.eventsNotifyByList;
    this.accountsList = dialogData.accountsList;
    this.contactsList = dialogData.contactsList;
  }

  ngOnInit(): void {
    this.isUserConsultant = this.userService.isUserConsultant();
    this.isUserBusinessman = this.userService.isUserBusinessman();

    // Set appropriate form field based on user type
    if (this.isUserConsultant) {
      this.eventsService.form.get('codigoConsultor')?.setValue(this.userCode);
    } else {
      this.eventsService.form.get('codigoEmpresario')?.setValue(this.userCode);
    }

    // Subscribe to value changes in start and end date/time fields to calculate duration
    this.eventsService.form
      .get('fechaHoraInicio')
      ?.valueChanges.subscribe(() => {
        this.calculateDifference();
      });

    this.eventsService.form.get('fechaHoraFin')?.valueChanges.subscribe(() => {
      this.calculateDifference();
    });
  }

  ngAfterViewInit(): void {
    // After view is initialized, select the event type and perform some actions
    setTimeout(() => {
      const selectedEventType = this.selectEventType.value;
      if (selectedEventType != null) {
        const option =
          this.selectEventType.options.toArray()[selectedEventType - 1];
        if (option) {
          const selectedEventTypeViewValue = option.viewValue;
          this.eventTypeCheck(selectedEventTypeViewValue);
        }
      }
    });
  }

  // Function to generate a range of numbers, used in 'Porcentaje de Progreso'
  generateRange(start: number, end: number, step: number): number[] {
    const range = [];
    for (let i = start; i <= end; i += step) {
      range.push(i);
    }
    return range;
  }

  // Function to calculate the duration between start and end dates
  calculateDifference() {
    const startDate = new Date(
      this.eventsService.form.get('fechaHoraInicio')?.value
    );
    const endDate = new Date(
      this.eventsService.form.get('fechaHoraFin')?.value
    );

    if (!isNaN(startDate.getTime()) && !isNaN(endDate.getTime())) {
      const minutesDifference =
        (endDate.getTime() - startDate.getTime()) / (1000 * 60);

      const roundedMinutesDifference = minutesDifference.toFixed(4);
      this.eventsService.form
        .get('tiempoDuracion')
        ?.setValue(roundedMinutesDifference);
    } else {
      this.eventsService.form
        .get('tiempoDuracion')
        ?.setValue('Ingrese las fechas adecuadas');
    }
  }

  // Function to handle form submission
  async onSubmit() {
    if (this.eventsService.form.valid) {
      const formValue: EventDTO = { ...this.eventsService.form.value };
      const code: number = this.eventsService.form.get('codigo')?.value;

      if (code) {
        await this.eventsService.update(code, formValue);
      } else {
        await this.eventsService.insert(formValue);
      }
      this.resetFormAndCloseDialog();
    }
  }

  // Function to handle closing dialog
  onClose() {
    this.resetFormAndCloseDialog();
  }

  // Function to handle cancellation
  onCancel() {
    this.resetFormAndCloseDialog();
  }

  // Private function to reset form and close dialog
  private resetFormAndCloseDialog() {
    this.eventsService.form.reset();
    this.eventsService.initializeFormGroup();
    this.dialogRef.close();
  }

  // Function to get event code from form
  getEventCode(): number | null {
    return this.eventsService.form.valid
      ? this.eventsService.form.get('codigo')?.value
      : null;
  }

  // Function to check event type and set corresponding flags
  eventTypeCheck(type: string | null) {
    if (type) {
      this.eventTypeIsWhatsapp = type === 'Chat Whatsapp';
      this.eventTypeIsEmail = type === 'Email';
    }
  }

  // Function to handle event type selection change
  onTypeSelectionChange(event: any) {
    const eventCode = this.getEventCode();

    if (eventCode) {
      const selectedEventType = event.source.triggerValue;
      this.eventTypeCheck(selectedEventType);
    }
  }
}
