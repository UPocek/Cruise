import { Component, OnDestroy } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { PopUpService } from 'src/app/universal-components/services/pop-up.service';
import { UserService } from '../../services/user.service';
import { ResetPasswordService } from '../../services/reset-password.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent implements OnDestroy {
  email?: string;

  resetPasswordForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
  });

  getUserSubscribtion: Subscription = new Subscription();
  resetPasswordSubscription: Subscription = new Subscription();
  constructor(
    private resetPasswordService: ResetPasswordService,
    private router: Router,
    private userService: UserService,
    private popUpService: PopUpService
  ) {}

  resetPassword() {
    if (this.resetPasswordForm.valid) {
      this.email = this.resetPasswordForm.value.email!;
      this.getUserSubscribtion = this.userService
        .getUserByEmail(this.email)
        .subscribe({
          next: (user) => {
            this.resetPasswordSubscription = this.resetPasswordService
              .forgotPassword(user.id)
              .subscribe({
                next: () => {
                  this.router.navigateByUrl('/reset-password-pending');
                },
                error: () => {
                  this.resetPasswordForm.reset();
                  this.popUpService.showPopUp('Unsuccesful. Try again later.');
                },
              });
          },
          error: () => {
            this.popUpService.showPopUp(
              'User with that email does not exist. Check your email and try again.'
            );
          },
        });
    }
  }

  ngOnDestroy(): void {
    this.resetPasswordSubscription.unsubscribe();
    this.getUserSubscribtion.unsubscribe();
  }
}
