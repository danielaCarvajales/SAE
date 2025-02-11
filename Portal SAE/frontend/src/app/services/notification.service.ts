import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(public matSnackBar: MatSnackBar) {}

  // Default time for notifications to stay on screen (in milliseconds).
  TIME_ON_SCREEN = 3000;

  /**
   * Display a success notification.
   * @param msg The message to display.
   * @param time Optional: The duration for the notification to stay on screen (in milliseconds).
   */
  success(msg: string, time: number = this.TIME_ON_SCREEN) {
    this.matSnackBar.open(msg, '', {
      duration: time,
      panelClass: ['notification', 'success'],
    });
  }

  /**
   * Display a warning notification.
   * @param msg The message to display.
   * @param time Optional: The duration for the notification to stay on screen (in milliseconds).
   */
  warn(msg: string, time: number = this.TIME_ON_SCREEN) {
    this.matSnackBar.open(msg, '', {
      duration: time,
      panelClass: ['notification', 'warn'],
    });
  }
}
