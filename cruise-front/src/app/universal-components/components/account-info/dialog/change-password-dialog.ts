import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';

import { PopUpService } from 'src/app/universal-components/services/pop-up.service';
import { PasswordService } from '../../../services/password.service';

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.html',
  styleUrls: ['./change-password-dialog.css'],
})

// eslint-disable-next-line @angular-eslint/component-class-suffix
export class ChangePasswordDialog {
  currentPassword!: string;
  newPassword!: string;
  confirmNewPassword!: string;

  constructor(
    private dialog: MatDialog,
    private passwordService: PasswordService,
    private popUpService: PopUpService
  ) {}

  changePassword() {
    if (this.newPassword !== this.confirmNewPassword) {
      this.popUpService.showPopUp('Passwords are not same');
      return;
    }

    this.passwordService
      .changePassword(this.currentPassword, this.newPassword)
      .subscribe({
        next: () => {
          this.dialog.closeAll();
        },
        error: () => {
          this.popUpService.showPopUp('Something went wrong! Try again later');
          this.dialog.closeAll();
        },
      });
  }
}
