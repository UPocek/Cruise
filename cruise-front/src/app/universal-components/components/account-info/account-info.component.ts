import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import * as SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

import { PopUpService } from '../../services/pop-up.service';
import { RegisteredUserDTO } from '../../../user/models/registered-user-dto';
import { PassengerService } from '../../../user/services/passenger.service';
import { DriverService } from '../../../driver/services/driver.service';
import { AdminService } from '../../../administrator/services/admin.service';
import { UserDTO } from '../../../user/models/user-dto';
import { AdminDTO } from '../../../user/models/admin-dto';
import { AuthService } from '../../../auth/services/auth.service';
import { AdminNotificationDTO } from '../../../administrator/models/admin-notification-dto';
import { ChangePasswordDialog } from './dialog/change-password-dialog';
import { Router } from '@angular/router';

@Component({
  selector: 'app-account-info',
  templateUrl: './account-info.component.html',
  styleUrls: ['./account-info.component.css'],
})
export class AccountInfoComponent implements OnInit, OnDestroy {
  user: RegisteredUserDTO = <RegisteredUserDTO>{};
  admin: AdminDTO = <AdminDTO>{};
  firstName?: string = '';
  lastName?: string = '';
  profilePicture: any = null;
  email_username?: string =
    localStorage.getItem('role') == 'ROLE_ADMIN' ? 'Username' : 'Email';

  changeInfoForm = new FormGroup({
    email: new FormControl('', [Validators.required]),
    phone: new FormControl('', [Validators.required]),
    address: new FormControl('', [Validators.required]),
  });

  stompClient: any;
  url: string = 'http://localhost:8080';
  isLoaded = false;

  getPassengerSubscription: Subscription = new Subscription();
  getDriverSubscription: Subscription = new Subscription();
  getAdminSubscription: Subscription = new Subscription();
  updatePassengerSubscription: Subscription = new Subscription();
  updateDriverSubscription: Subscription = new Subscription();
  updateAdminSubscription: Subscription = new Subscription();

  constructor(
    private passengerService: PassengerService,
    private driverService: DriverService,
    private adminService: AdminService,
    private authService: AuthService,
    private dialog: MatDialog,
    private popUpService: PopUpService,
    private router: Router
  ) {}

  ngOnInit(): void {
    let email = this.authService.getEmail();
    if (this.authService.getRole() === 'ROLE_PASSENGER') {
      this.getPassengerSubscription = this.passengerService
        .getPassenger(email)
        .subscribe((response) => {
          this.user = response;
          this.firstName = response.name;
          this.lastName = response.surname;
          this.profilePicture =
            response.profilePicture == 'data:image/jpeg;base64,'
              ? 'assets/logo.png'
              : response.profilePicture;

          this.changeInfoForm = new FormGroup({
            email: new FormControl(this.user.email, [Validators.required]),
            phone: new FormControl(this.user.telephoneNumber, [
              Validators.required,
            ]),
            address: new FormControl(this.user.address, [Validators.required]),
          });
        });
    } else if (this.authService.getRole() === 'ROLE_DRIVER') {
      this.getDriverSubscription = this.driverService
        .getDriver(email)
        .subscribe((response) => {
          this.user = response;
          this.firstName = response.name;
          this.lastName = response.surname;
          this.profilePicture = response.profilePicture;

          this.changeInfoForm = new FormGroup({
            email: new FormControl(this.user.email, [Validators.required]),
            phone: new FormControl(this.user.telephoneNumber, [
              Validators.required,
            ]),
            address: new FormControl(this.user.address, [Validators.required]),
          });
        });
    } else {
      this.getAdminSubscription = this.adminService
        .getAdmin(email)
        .subscribe((response) => {
          this.admin = response;
          this.firstName = response.name;
          this.lastName = response.surname;
          this.profilePicture = response.profilePicture;

          this.changeInfoForm = new FormGroup({
            email: new FormControl(this.admin.username, [Validators.required]),
            phone: new FormControl(this.admin.telephoneNumber, [
              Validators.required,
            ]),
            address: new FormControl(this.admin.address, [Validators.required]),
          });
        });
    }

    let that = this;

    this.stompClient = Stomp.over(function () {
      return new SockJS(`${that.url}/socket`);
    });

    this.stompClient.connect({}, () => {});
  }

  checkIfFileValid(event: any): boolean {
    if (event.target.files[0] && event.target.files[0].name.length > 0) {
      const fileType = event.target.files[0].type;
      // if file is not an image
      if (fileType.match('image/*') != null) {
        return true;
      }
      return false;
    }
    return false;
  }

  selectProfilePicture(event: any) {
    if (this.checkIfFileValid(event)) {
      let reader = new FileReader();
      reader.readAsDataURL(event.target.files[0]);
      reader.onload = (_event: any) => {
        this.profilePicture = reader.result;
      };
    }
  }

  changeInfo() {
    if (this.authService.getRole() === 'ROLE_PASSENGER') {
      const updatedUser: UserDTO = <UserDTO>{
        name: this.user?.name,
        surname: this.user?.surname,
        email: this.changeInfoForm.value.email,
        telephoneNumber: this.changeInfoForm.value.phone,
        address: this.changeInfoForm.value.address,
        profilePicture: this.profilePicture,
      };
      this.updatePassengerSubscription = this.passengerService
        .updatePassenger(this.user?.id.toString(), updatedUser)
        .subscribe({
          next: (response) => {
            window.location.reload();
          },
          error: (error: HttpErrorResponse) => {
            this.popUpService.showPopUp(error.error.message);
          },
        });
    } else if (this.authService.getRole() === 'ROLE_DRIVER') {
      const updatedUser: UserDTO = <UserDTO>{
        name: this.user?.name,
        surname: this.user?.surname,
        email: this.changeInfoForm.value.email,
        telephoneNumber: this.changeInfoForm.value.phone,
        address: this.changeInfoForm.value.address,
      };
      this.updateDriverSubscription = this.driverService
        .requestChanges(this.user?.id.toString(), updatedUser)
        .subscribe({
          next: (response) => {
            this.stompClient.send(
              '/socket-in/driver-change-notification',
              {},
              JSON.stringify(<AdminNotificationDTO>(<unknown>{
                isPanic: false,
                driverChanges: response,
                time: response.time,
              }))
            );
            window.location.reload();
          },
          error: (error: HttpErrorResponse) => {
            this.popUpService.showPopUp(error.error.message);
          },
        });
    } else {
      const updatedUser: AdminDTO = <AdminDTO>{
        id: this.admin.id,
        name: this.admin?.name,
        surname: this.admin?.surname,
        username: this.changeInfoForm.value.email,
        telephoneNumber: this.changeInfoForm.value.phone,
        address: this.changeInfoForm.value.address,
        profilePicture: this.profilePicture,
      };
      this.updateAdminSubscription = this.adminService
        .updateAdmin(this.admin?.id.toString(), updatedUser)
        .subscribe({
          next: (response) => {
            window.location.reload();
          },
          error: (error: HttpErrorResponse) => {
            this.popUpService.showPopUp(error.error.message);
          },
        });
    }
  }

  openDialog() {
    this.dialog.open(ChangePasswordDialog);
  }

  ngOnDestroy() {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
    this.getPassengerSubscription.unsubscribe();
    this.getDriverSubscription.unsubscribe();
    this.getAdminSubscription.unsubscribe();
    this.updatePassengerSubscription.unsubscribe();
    this.updateDriverSubscription.unsubscribe();
    this.updateAdminSubscription.unsubscribe();
  }
}
