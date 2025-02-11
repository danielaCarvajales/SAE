import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  constructor(private router: Router) {}

  // Redirect to '/cargando' route when component initializes
  ngOnInit(): void {
    this.router.navigate(['/cargando']);
  }
}
