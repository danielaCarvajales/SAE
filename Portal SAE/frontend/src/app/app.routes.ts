import { Routes } from '@angular/router';
import { LoadingScreenComponent } from './pages/loading-screen/loading-screen.component';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { AccountsComponent } from './pages/home/accounts/accounts.component';
import { ContactsComponent } from './pages/home/accounts/contacts/contacts.component';
import { ActivitiesComponent } from './pages/home/tasks/activities/activities.component';
import { DashboardComponent } from './pages/home/dashboard/dashboard.component';
import { EventsComponent } from './pages/home/events/events.component';
import { FaqsComponent } from './pages/home/faqs/faqs.component';
import { IncidentsComponent } from './pages/home/incidents/incidents.component';
import { ManualsComponent } from './pages/home/manuals/manuals.component';
import { ReportActivitiesComponent } from './pages/home/reports/report-activities/report-activities.component';
import { ReportEventsComponent } from './pages/home/reports/report-events/report-events.component';
import { ReportIncidentsComponent } from './pages/home/reports/report-incidents/report-incidents.component';
import { ReportTasksComponent } from './pages/home/reports/report-tasks/report-tasks.component';
import { ReportsComponent } from './pages/home/reports/reports.component';
import { TasksComponent } from './pages/home/tasks/tasks.component';
import { WhatsappComponent } from './pages/home/whatsapp/whatsapp.component';
import { EmailComponent } from './pages/home/email/email.component';
import { ConsultantsComponent } from './pages/home/consultants/consultants.component';
import { EmailConfComponent } from './pages/home/email/email-conf/email-conf.component';

export const routes: Routes = [
  { path: 'cargando', component: LoadingScreenComponent },
  { path: 'ingreso', component: LoginComponent },
  {
    path: 'hogar',
    component: HomeComponent,
    children: [
      {
        path: '',
        component: DashboardComponent,
      },
      {
        path: 'cuentas',
        component: AccountsComponent,
      },
      {
        path: 'cuentas/contactos/:id',
        component: ContactsComponent,
      },
      {
        path: 'tareas',
        component: TasksComponent,
      },
      {
        path: 'tareas/actividades/:id',
        component: ActivitiesComponent,
      },
      {
        path: 'eventos',
        component: EventsComponent,
      },
      {
        path: 'faqs',
        component: FaqsComponent,
      },
      {
        path: 'incidencias',
        component: IncidentsComponent,
      },
      {
        path: 'manuales',
        component: ManualsComponent,
      },
      {
        path: 'informes',
        component: ReportsComponent,
      },
      {
        path: 'informes/tareas',
        component: ReportTasksComponent,
      },
      {
        path: 'informes/actividades',
        component: ReportActivitiesComponent,
      },
      {
        path: 'informes/eventos',
        component: ReportEventsComponent,
      },
      {
        path: 'informes/incidencias',
        component: ReportIncidentsComponent,
      },
      {
        path: 'whatsapp',
        component: WhatsappComponent,
      },
      {
        path: 'email',
        component: EmailComponent,
      },
      {
        path: 'configuracion-email',
        component: EmailConfComponent,
      },
      {
        path: 'consultores',
        component: ConsultantsComponent,
      },
    ],
  },
];
