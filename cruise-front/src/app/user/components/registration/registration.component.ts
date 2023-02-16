import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

import { RegisteredUserDTO } from '../../models/registered-user-dto';
import { UserDTO } from '../../models/user-dto';
import { RegistrationService } from '../../services/registration.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent {
  registeredUserDTO?: RegisteredUserDTO | null = null;
  hide: boolean = true;
  hide_re: boolean = true;
  form_submited: boolean = false;

  constructor(
    private registrationService: RegistrationService,
    private router: Router,
    private popUpService: PopUpService
  ) {}

  registrationForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    name: new FormControl('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(20),
    ]),
    surname: new FormControl('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(20),
    ]),
    phone: new FormControl('', [
      Validators.required,
      Validators.pattern(
        '^[+]?[(]?[0-9]{3}[)]?[-s.]?[0-9]{3}[-s.]?[0-9]{4,6}$'
      ),
    ]),
    address: new FormControl('', [
      Validators.required,
      Validators.minLength(1),
      Validators.maxLength(100),
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(20),
    ]),
    repassword: new FormControl('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(20),
    ]),
  });

  registerUser() {
    if (!this.registrationForm.valid) {
      this.popUpService.showPopUp(
        'Registration unsucessfull! Form fields not filled with valid inputs'
      );
      return;
    }
    if (
      this.registrationForm.value.password !==
      this.registrationForm.value.repassword
    ) {
      this.popUpService.showPopUp(
        'Registration unsucessfull! Passwords do not match'
      );
      return;
    }

    this.form_submited = true;

    const userDTO: UserDTO = <UserDTO>{
      name: this.registrationForm.value.name,
      surname: this.registrationForm.value.surname,
      profilePicture: 'data:image/jpeg;base64, ',
      telephoneNumber: this.registrationForm.value.phone,
      email: this.registrationForm.value.email,
      address: this.registrationForm.value.address,
      password: this.registrationForm.value.password,
    };
    this.registrationService.registerUser(userDTO).subscribe({
      next: (response) => {
        this.registeredUserDTO = response;
        this.router.navigateByUrl('/verification-pending');
      },
      error: (error: HttpErrorResponse) => {
        this.registrationForm.reset();
        this.popUpService.showPopUp(error.message);
      },
    });
  }
}
