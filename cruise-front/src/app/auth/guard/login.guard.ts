import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { NabvarService } from '../../universal-components/services/nabvar.service';

@Injectable({
  providedIn: 'root',
})
export class LoginGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
    private navbarService: NabvarService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    if (this.authService.isLoggedIn()) {
      this.navbarService.setRole(this.authService.getRole());
      if (this.authService.getRole() === 'ROLE_ADMIN') {
        this.router.navigate(['admin-main']);
      } else if (this.authService.getRole() === 'ROLE_PASSENGER') {
        this.router.navigate(['passenger-main']);
      } else {
        this.router.navigate(['driver-main']);
      }
      return false;
    }
    this.navbarService.setRole("")
    return true;
  }
}
