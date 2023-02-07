import { Component, Input, OnInit } from '@angular/core';
import { UserDTO } from '../../../user/models/user-dto';
import { UserInfoService } from '../../services/user-info.service';
import {MatDialog} from "@angular/material/dialog";
import {
  ChangePasswordDialog
} from "../../../universal-components/components/account-info/dialog/change-password-dialog";
import {NotesDialog} from "./dialog/notes-dialog";

@Component({
  selector: 'app-user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css'],
})
export class UserInfoComponent implements OnInit {
  @Input() isPassenger: boolean = true;
  activity: string = 'NOT BLOCKED';
  isChecked?: boolean;
  user?: UserDTO;
  id?: number;
  name?: string;
  surname?: string;
  email?: string;
  phone?: string;
  address?: string;
  profilePicture?: string = 'assets/logo.png';

  constructor(private userInfoService: UserInfoService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.userInfoService.selectedProduct$.subscribe((value) => {
      this.user = value;
      this.name = value.name;
      this.surname = value.surname;
      this.email = value.email;
      this.phone = value.telephoneNumber;
      this.address = value.address;
      this.isChecked = value.blocked;
      this.profilePicture =
        value.profilePicture == 'data:image/jpeg;base64,'
          ? 'assets/logo.png'
          : value.profilePicture;
      if (this.isChecked) this.activity = 'BLOCKED';
      this.userInfoService.getUserByEmail(this.email!).subscribe((user) => {
        this.id = user.id
      })

    });
  }
  onToggle(event: any) {
    if (event.checked) {
      if (this.isPassenger) {
        this.userInfoService
          .blockPassenger(this.user!.email)
          .subscribe((response) => {
            this.activity = 'BLOCKED';
          });
      } else {
        this.userInfoService
          .blockDriver(this.user!.email)
          .subscribe((response) => {
            this.activity = 'BLOCKED';
          });
      }
    } else {
      if (this.isPassenger) {
        this.userInfoService
          .unblockPassenger(this.user!.email)
          .subscribe((response) => {
            this.activity = 'NOT BLOCKED';
          });
      } else {
        this.userInfoService
          .unblockDriver(this.user!.email)
          .subscribe((response) => {
            this.activity = 'NOT BLOCKED';
          });
      }
    }
  }

  openNotesDialog() {
    this.dialog.open(NotesDialog);
    this.userInfoService.setId(this.id!)
  }
}
