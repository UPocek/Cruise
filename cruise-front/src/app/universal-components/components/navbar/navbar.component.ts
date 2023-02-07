import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { DriverService } from 'src/app/driver/services/driver.service';
import { LoginService } from 'src/app/auth/services/login.service';
import { NabvarService } from '../../services/nabvar.service';
import { AuthService } from '../../../auth/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit, OnDestroy {
  role: string = '';
  mainPage: string = '';
  navbarItems: string[] = [];
  navbarLinks: string[] = [];
  private subscription: Subscription = new Subscription();

  constructor(
    private navbarService: NabvarService,
    private driverService: DriverService,
    private loginService: LoginService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.subscription = this.authService.userState$.subscribe(
      (response: string) => {
        this.role = response;

        if (this.role === 'ROLE_PASSENGER') {
          this.navbarItems = this.navbarService.getPassengerNavbar();
          this.navbarLinks = this.navbarService.getPassengerLinks();
        } else if (this.role === 'ROLE_DRIVER') {
          this.navbarItems = this.navbarService.getDriverNavbar();
          this.navbarLinks = this.navbarService.getDriverLinks();
        } else if (this.role === 'ROLE_ADMIN') {
          this.navbarItems = this.navbarService.getAdminNavbar();
          this.navbarLinks = this.navbarService.getAdminLinks();
        } else {
          this.navbarItems = this.navbarService.getUnregisteredUserNavbar();
          this.navbarLinks = this.navbarService.getUnregisteredUserLinks();
        }
      }
    );
  }

  logOut() {
    if (this.role === 'ROLE_DRIVER') {
      let driverId = 0;
      this.loginService.registeredUser$.subscribe((response) => {
        driverId = response.id;
      });
      this.driverService.logOutDriver(driverId);
    }
    window.localStorage.clear();
    this.authService.setUser();
    this.router.navigateByUrl('/log-in');
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
