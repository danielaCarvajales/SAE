export interface CreateIncidentDTO {
  pregunta: string;
  respuesta: string;
  fechaHoraInicio: string;
  fechaHoraFin: string;
  codigoConsultor: string;
  codigoEmpresario: string;
  categoria: string;
  estado: string;
}
