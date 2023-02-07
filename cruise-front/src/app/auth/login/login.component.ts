import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { NabvarService } from 'src/app/universal-components/services/nabvar.service';
import { UserDTO } from '../../user/models/user-dto';
import { LoginDTO } from '../models/login-dto';
import { LoginService } from '../services/login.service';
import { DriverService } from '../../driver/services/driver.service';
import { AuthService } from '../services/auth.service';
import { CredentialsDTO } from '../models/credentials-dto';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  user?: UserDTO | null;
  loggedUser?: LoginDTO | null;

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
  });

  hide: boolean = true;

  constructor(
    private loginService: LoginService,
    private router: Router,
    private navbarService: NabvarService,
    private driverService: DriverService,
    private authService: AuthService,
    private popUpService: PopUpService
  ) {}

  ngOnInit(): void {
    this.navbarService.setRole('');
  }

  loginUser() {
    if (this.loginForm.valid) {
      const loginDTO: CredentialsDTO = <CredentialsDTO>{
        email: this.loginForm.value.email,
        password: this.loginForm.value.password,
      };
      this.loginService.loginUser(loginDTO).subscribe({
        next: (response) => {
          {
            this.loggedUser = response;
            if (this.loggedUser !== null) {
              window.localStorage.clear();
              window.localStorage.setItem('jwt', response.accessToken);
              window.localStorage.setItem(
                'refreshToken',
                response.refreshToken
              );
              window.localStorage.setItem('id', this.authService.getId());
              if (this.authService.getRole() === 'ROLE_PASSENGER') {
                this.loginService
                  .getUser(this.loginForm.value.email!)
                  .subscribe({
                    next: (response) => {
                      if (!response) {
                        this.authService.setUser();
                        this.navbarService.setRole(this.authService.getRole());
                        this.router.navigateByUrl('/passenger-main');
                      } else {
                        window.localStorage.clear();
                        this.popUpService.showPopUp('User is blocked');
                      }
                    },
                    error: (err) => {
                      this.popUpService.showPopUp("User doesn't exist");
                    },
                  });
              } else if (this.authService.getRole() === 'ROLE_DRIVER') {
                this.driverService
                  .getDriver(this.authService.getEmail())
                  .subscribe((response) => {
                    this.loginService.setRegisteredUser(response);
                  });
                this.loginService
                  .getUser(this.loginForm.value.email!)
                  .subscribe({
                    next: (response) => {
                      if (!response) {
                        this.authService.setUser();
                        this.navbarService.setRole(this.authService.getRole());
                        this.router.navigateByUrl('/driver-main');
                      } else {
                        window.localStorage.clear();
                        this.popUpService.showPopUp('User is blocked');
                      }
                    },
                    error: (err) => {
                      this.popUpService.showPopUp("User doesn't exist");
                    },
                  });
              } else if (this.authService.getRole() === 'ROLE_ADMIN') {
                this.authService.setUser();
                this.navbarService.setRole(this.authService.getRole());
                this.router.navigateByUrl('/admin-main');
              }
            } else {
              this.loginForm.reset();
              this.popUpService.showPopUp('Login unsucessfull');
            }
          }
        },
        error: (error) => {
          this.loginForm.reset();
          this.popUpService.showPopUp('Login unsucessfull');
        },
      });
    } else {
      this.popUpService.showPopUp('Login unsucessfull');
      this.loginForm.reset();
    }
  }
}
