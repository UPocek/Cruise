import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DriverChangesDTO } from '../../models/driver-changes-dto';
import { DriverInfoChangesService } from '../../services/driver-info-changes.service';
import { UserDTO } from '../../../user/models/user-dto';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

@Component({
  selector: 'app-driver-changes-approval',
  templateUrl: './driver-changes-approval.component.html',
  styleUrls: ['./driver-changes-approval.component.css'],
})
export class DriverChangesApprovalComponent implements OnInit, OnDestroy {
  driverChangeInfo?: DriverChangesDTO;

  firstName?: string = '';
  lastName?: string = '';

  changeInfoForm = new FormGroup({
    email: new FormControl('', [Validators.required]),
    phone: new FormControl('', [Validators.required]),
    address: new FormControl('', [Validators.required]),
  });

  approveSubscription: Subscription = new Subscription();
  rejectSubscription: Subscription = new Subscription();

  constructor(
    private driverInfoChangesService: DriverInfoChangesService,
    private router: Router,
    private popUpService: PopUpService
  ) {}

  ngOnInit(): void {
    this.driverInfoChangesService.selectedProduct$.subscribe((value) => {
      this.driverChangeInfo = value;
      this.firstName = value.name;
      this.lastName = value.surname;

      this.changeInfoForm = new FormGroup({
        email: new FormControl(value.email, [Validators.required]),
        phone: new FormControl(value.telephoneNumber, [Validators.required]),
        address: new FormControl(value.address, [Validators.required]),
      });
    });
  }

  changeInfo() {}
  approve() {
    const userDTO: UserDTO = <UserDTO>{
      name: this.driverChangeInfo?.name,
      surname: this.driverChangeInfo?.surname,
      profilePicture: this.driverChangeInfo?.profilePicture,
      telephoneNumber: this.driverChangeInfo?.telephoneNumber,
      email: this.driverChangeInfo?.email,
      address: this.driverChangeInfo?.address,
    };
    this.approveSubscription = this.driverInfoChangesService
      .approve(
        this.driverChangeInfo?.driverId,
        this.driverChangeInfo?.id,
        userDTO
      )
      .subscribe((value) => {
        this.popUpService.showPopUp('Approve completed!');
        this.router.navigateByUrl('/admin-notifications');
      });
  }
  reject() {
    this.rejectSubscription = this.driverInfoChangesService
      .reject(this.driverChangeInfo?.id)
      .subscribe(() => {
        this.popUpService.showPopUp('Approve completed!');
        this.router.navigateByUrl('/admin-notifications');
      });
  }

  ngOnDestroy(): void {
    this.approveSubscription.unsubscribe();
    this.rejectSubscription.unsubscribe();
  }
}
