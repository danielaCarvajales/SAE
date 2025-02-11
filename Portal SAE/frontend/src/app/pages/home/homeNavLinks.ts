export interface NavLink {
  label: string;
  url: string;
  icon: string;
  isLocalIcon: boolean;
}

// Navigation links for the home component
export const navLinks: NavLink[] = [
  {
    label: 'Dashboard',
    url: '/hogar',
    icon: 'home',
    isLocalIcon: false,
  },
  {
    label: 'Cuentas',
    url: '/hogar/cuentas',
    icon: 'group',
    isLocalIcon: false,
  },
  {
    label: 'Incidencias',
    url: '/hogar/incidencias',
    icon: 'local_activity',
    isLocalIcon: false,
  },
  {
    label: 'Tareas',
    url: '/hogar/tareas',
    icon: 'task',
    isLocalIcon: false,
  },
  {
    label: 'Eventos',
    url: '/hogar/eventos',
    icon: 'event',
    isLocalIcon: false,
  },
  {
    label: 'Preguntas Frecuentes',
    url: '/hogar/faqs',
    icon: 'quiz',
    isLocalIcon: false,
  },
  {
    label: 'Informes',
    url: '/hogar/informes',
    icon: 'query_stats',
    isLocalIcon: false,
  },
  {
    label: 'Manuales',
    url: '/hogar/manuales',
    icon: 'menu_book',
    isLocalIcon: false,
  },
  {
    label: 'Whatsapp',
    url: '/hogar/whatsapp',
    icon: 'assets/Icons/WhatsApp.svg',
    isLocalIcon: true,
  },
  {
    label: 'Correo Electronico',
    url: '/hogar/email',
    icon: 'email',
    isLocalIcon: false,
  },
];
