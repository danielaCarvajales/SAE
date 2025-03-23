import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../../services/user.service';
import { NavLink, navLinks } from './homeNavLinks';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    MatSidenavModule,
    MatListModule,
    RouterModule,
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatTooltipModule,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  userName!: string | null;
  userRole!: string | null;
  userCode!: number | null;
  isUserBusinessman!: boolean;

  sideBarOpen: boolean = false;
  sideNavLinks: NavLink[] = navLinks;

  // Verifies if client is Consultant or not (adds links to additional pages)
  ngOnInit(): void {
    this.userCode = this.userService.getUserCode();
    this.userRole = this.userService.getUserRole();
    this.userName = this.userService.getUserName();
    this.isUserBusinessman = this.userService.isUserBusinessman();

    this.sideNavLinks = [...navLinks];

    if (this.isUserBusinessman) {
      this.sideNavLinks.push({
        label: 'Consultores',
        url: '/hogar/consultores',
        icon: 'badge',
        isLocalIcon: false,
      });
    }
  } 

    /* ngOnInit(): void {
      this.userCode = this.userService.getUserCode();
      this.userRole = this.userService.getUserRole();
      this.userName = this.userService.getUserName();
      this.isUserBusinessman = this.userService.isUserBusinessman();
    
      // Redirigir siempre al Dashboard al iniciar sesión
      this.router.navigate(['/hogar']);
    
      // Clonar `navLinks` y actualizar el enlace de correo electrónico según la sesión
      this.sideNavLinks = navLinks.map(link => {
        if (link.label === 'Correo Electrónico') {
          return {
            ...link,
            url: this.userName ? '/hogar/email' : '/hogar/email-conf',
          };
        }
        return link;
      });
    
      if (this.isUserBusinessman) {
        this.sideNavLinks.push({
          label: 'Consultores',
          url: '/hogar/consultores',
          icon: 'badge',
          isLocalIcon: false,
        });
      }
    } */
    

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/ingreso']);
  }
}
