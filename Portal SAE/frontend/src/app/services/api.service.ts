import axios from 'axios';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  public API_URL = environment.API_URL;

  constructor(private userService: UserService) {}

  async login(email: string, password: string): Promise<any> {
    try {
      console.log('Enviando solicitud de login al backend:', { email, password });
      const response = await axios.post(`${this.API_URL}/email/fetch`, {
        email,
        password, // Ajusta la clave si el backend la espera como "password"
      });
  
      if (response.status === 200) {
        console.log('Login exitoso, almacenando credenciales');
        // Almacenar credenciales encriptadas en sessionStorage
        this.userService.storeEmailCredentials(email, password);
        return response.data;
      } else {
        throw new Error('Credenciales incorrectas');
      }
    } catch (error) {
      console.error('Error en login:', error);
      throw error;
    }
  }
  
  

  async getEmails(): Promise<any> {
    const { email, password } = this.userService.getStoredCredentials();
  
    if (!email || !password) {
      throw new Error('No se encontraron credenciales almacenadas');
    }
  
    try {
      console.log('Recuperando correos con:', email);
      const response = await axios.post(`${this.API_URL}/email/fetch`, { email, password });
      return response.data;
    } catch (error) {
      console.error('Error al obtener correos:', error);
      throw error;
    }
  }
  

  /**
   * Method to perform a GET request for a specified endpoint.
   * @param endpoint The endpoint to send the GET request to.
   */

  async get(endpoint: string, sendHeader: boolean = true): Promise<any> {
    try {
      const token = this.userService.getToken();
      const headers = sendHeader && token ? { Authorization: token } : {};
      const response = await axios.get(`${this.API_URL}/${endpoint}`, { headers });
      return response.data;
    } catch (error) {
      console.error(`Error en GET ${endpoint}:`, error);
      throw error;
    }
  }
  

  /**
   * Method to perform a POST request for a specified endpoint.
   * @param endpoint The endpoint to send the POST request to.
   * @param data The data to be sent as JSON body.
   */
  async post(
    endpoint: string,
    datos: any,
    sendHeader: boolean = true
  ): Promise<any> {
    const token = this.userService.getToken();
    const headers = sendHeader ? { Authorization: token } : {};
    const response = await axios.post(`${this.API_URL}/${endpoint}`, datos, {
      headers,
    });
    return response.data;
  }

  /**
   * Method to perform a PUT request for a specified endpoint.
   * @param endpoint The endpoint to send the PUT request to.
   * @param data The data to be sent as JSON body.
   */
  async put(
    endpoint: string,
    datos: any,
    sendHeader: boolean = true
  ): Promise<any> {
    const token = this.userService.getToken();
    const headers = sendHeader ? { Authorization: token } : {};
    const response = await axios.put(`${this.API_URL}/${endpoint}`, datos, {
      headers,
    });
    return response.data;
  }

  /**
   * Method to perform a DELETE request for a specified endpoint.
   * @param endpoint The endpoint to send the DELETE request to.
   */
  async delete(endpoint: string, sendHeader: boolean = true): Promise<any> {
    const token = this.userService.getToken();
    const headers = sendHeader ? { Authorization: token } : {};
    const response = await axios.delete(`${this.API_URL}/${endpoint}`, {
      headers,
    });
    return response.data;
  }

  /**
   * Method to perform GET requests for multiple endpoints simultaneously.
   * @param endpoints An array of endpoint strings to send the GET requests to.
   */
  async getMultiple(
    endpoints: string[],
    sendHeader: boolean = true
  ): Promise<any[]> {
    const token = this.userService.getToken();
    const headers = sendHeader ? { Authorization: token } : {};
    const requests = endpoints.map(endpoint =>
      axios.get(`${this.API_URL}/${endpoint}`, { headers })
    );
    const responses = await Promise.all(requests);

    // Extracting data from each response
    const responseData = responses.map(response => response.data);

    return responseData;
  }

  /**
   * Method to upload an image to the server.
   * @param endpoint The endpoint to send the POST request to.
   * @param file The file to be uploaded.
   */
  async postImage(endpoint: string, file: File): Promise<any> {
    const token = this.userService.getToken();
    try {
      const formData: FormData = new FormData();
      formData.append('file', file, file.name);

      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: `${token}`,
      };

      const response = await axios.post(
        `${this.API_URL}/${endpoint}`,
        formData,
        { headers }
      );
      return response.data;
    } catch (error) {
      throw new Error(
        'Error al cargar la imagen. Por favor, inténtalo de nuevo más tarde.'
      );
    }
  }

  
  
}
