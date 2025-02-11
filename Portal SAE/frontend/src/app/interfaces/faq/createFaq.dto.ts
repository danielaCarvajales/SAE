export interface CreateFaqDTO {
  pregunta: string;
  respuesta: string;
  aprobadoSuperadmin: boolean;
  categoria: string;
  preguntaRealizadaPor: string;
  respuestaRealizadaPor: string;
}
