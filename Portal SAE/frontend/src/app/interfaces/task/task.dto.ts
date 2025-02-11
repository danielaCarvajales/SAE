export interface TaskDTO {
  codigo: number;
  asunto: string;
  fechaHoraInicio: string;
  fechaHoraFin: string;
  porcentajeProgreso: string;
  descripcion: string;
  observacion: string;

  consultorAsignado: string;
  empresarioAsignado: string;
  cuentaAsignada: string;
  estado: string;
  tipo: string;
}
