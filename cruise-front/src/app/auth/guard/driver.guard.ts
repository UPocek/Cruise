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
export class DriverGuard implements CanActivate {
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
    | UrlTree
  {
    if(this.authService.isLoggedIn())
    {
      if(this.authService.getRole() === "ROLE_DRIVER")
        return true;
      else if(this.authService.getRole() === "ROLE_PASSENGER")
        this.router.navigateByUrl('/passenger-main')
      else
        this.router.navigateByUrl('/admin-main')
    }
    return false;
  }
}
