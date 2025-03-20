import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private encryptionKey = 'clave-segura-para-cifrado';
  constructor(private router: Router) {}

  /**
   * Retrieves the user email configuration from local storage.
   * @returns The user email configuration if found, otherwise null.
   */
  getEmailConf(): string | null {
    return localStorage.getItem('email-conf');
  }

  /**
   * Retrieves the user token from local storage.
   * @returns The user token if found, otherwise null.
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * Decodes the user token from local storage.
   * @returns The user's decoded token if found, otherwise null.
   */
  getDecodedToken(): any {
    const token = this.getToken();
    if (!token) {
      this.router.navigate(['ingreso']);
      return null;
    }
    try {
      return jwtDecode(token);
    } catch (error) {
      console.error('Error decoding token:', error);
      this.router.navigate(['ingreso']);
      return null;
    }
  }
  

  /**
   * Retrieves the user code from Token.
   * @returns The user code if found, otherwise null.
   */
  getUserCode(): number | null {
    const decodedToken = this.getDecodedToken();
    return decodedToken.jti ? parseInt(decodedToken.jti, 10) : null;
  }

  /**
   * Retrieves the user role from Token.
   * @returns The user role if found, otherwise null.
   */
  getUserRole(): string | null {
    const decodedToken = this.getDecodedToken();
    return decodedToken.rol ? decodedToken.rol.toLowerCase() : null;
  }

  /**
   * Checks if the user role is 'empresario' (businessman).
   * @returns True if the user role is 'empresario', otherwise false.
   */
  isUserBusinessman(): boolean {
    return this.getUserRole() === 'empresario';
  }

  /**
   * Checks if the user role is 'consultor' (consultant).
   * @returns True if the user role is 'consultor', otherwise false.
   */
  isUserConsultant(): boolean {
    return this.getUserRole() === 'consultor';
  }

  /**
   * Retrieves the user name from Token.
   * @returns The user name if found, otherwise null.
   */
  getUserName(): string | null {
    const decodedToken = this.getDecodedToken();
    return decodedToken.sub;
  }

  /**
   * Retrieves the user email from Token.
   * @returns The user email if found, otherwise null.
   */
  getUserEmail(): string | null {
    const decodedToken = this.getDecodedToken();
    return decodedToken.email;
  }

  /**
   * Retrieves the user phone number from Token.
   * @returns The user phone number if found, otherwise null.
   */
  getUserPhoneNumber(): string | null {
    const decodedToken = this.getDecodedToken();
    return decodedToken.numeroMovil;
  }

  storeEmailCredentials(email: string, password: string): void {
    const encryptedEmail = CryptoJS.AES.encrypt(email, this.encryptionKey).toString();
    const encryptedPassword = CryptoJS.AES.encrypt(password, this.encryptionKey).toString();
  
    sessionStorage.setItem('email', encryptedEmail);
    sessionStorage.setItem('password', encryptedPassword);
  }
  
  getEmail(): string | null {
    const encryptedEmail = sessionStorage.getItem('email');
    return encryptedEmail ? CryptoJS.AES.decrypt(encryptedEmail, this.encryptionKey).toString(CryptoJS.enc.Utf8) : null;
  }
  
  getPassword(): string | null {
    const encryptedPassword = sessionStorage.getItem('password');
    return encryptedPassword ? CryptoJS.AES.decrypt(encryptedPassword, this.encryptionKey).toString(CryptoJS.enc.Utf8) : null;
  }

  getStoredCredentials(): { email: string | null; password: string | null } {
    const encryptedEmail = sessionStorage.getItem('email');
    const encryptedPassword = sessionStorage.getItem('password');
  
    if (!encryptedEmail || !encryptedPassword) {
      return { email: null, password: null };
    }
  
    try {
      const email = CryptoJS.AES.decrypt(encryptedEmail, this.encryptionKey).toString(CryptoJS.enc.Utf8);
      const password = CryptoJS.AES.decrypt(encryptedPassword, this.encryptionKey).toString(CryptoJS.enc.Utf8);
      return { email, password };
    } catch (error) {
      console.error('Error al desencriptar las credenciales:', error);
      return { email: null, password: null };
    }
  }
  

  /**
   * Limpia las credenciales del usuario al cerrar sesión.
   */
  clearEmailCredentials(): void {
    sessionStorage.removeItem('email');
    sessionStorage.removeItem('password');
  }
}
