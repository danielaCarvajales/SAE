export interface EmailDTO {
  codigo: string;
  remitente: string;
  destinatario: string;
  asunto: string;
  contenido: string;
  fechaEnviado: string;
  fechaRecibido: string;
  eventoAsignado: number;
  adjunto : File;
}
