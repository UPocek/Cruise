import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NewPasswordService } from '../../services/new-password.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ResetPasswordDTO } from '../../models/reset-password-dto';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

@Component({
  selector: 'app-new-password',
  templateUrl: './new-password.component.html',
  styleUrls: ['./new-password.component.css'],
})
export class NewPasswordComponent implements OnInit {
  newPasswordForm = new FormGroup({
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

  hide: boolean = true;
  hide_re: boolean = true;

  id: number = 0;
  code: string = '';

  constructor(
    private newPasswordService: NewPasswordService,
    private route: ActivatedRoute,
    private router: Router,
    private popUpService: PopUpService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.id = params['id'];
      this.code = params['code'];
    });
  }

  changePassword() {
    if (
      this.newPasswordForm.value.password !=
      this.newPasswordForm.value.repassword
    ) {
      this.popUpService.showPopUp(
        'Registration unsucessfull! Passwords do not match'
      );
      return;
    }

    const request: ResetPasswordDTO = <ResetPasswordDTO>{
      new_password: this.newPasswordForm.value.password,
      code: this.code,
    };

    this.newPasswordService.changePassword(this.id, request).subscribe(
      (response) => {
        this.popUpService.showPopUp('Password changed successfully');
        this.router.navigateByUrl('/log-in');
      },
      (error) => {
        this.popUpService.showPopUp('Password not valid. Try another one.');
      }
    );
  }
}
