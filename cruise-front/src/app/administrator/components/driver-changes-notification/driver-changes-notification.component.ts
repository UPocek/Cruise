import { Component, HostListener, Input, OnInit } from '@angular/core';
import { DriverChangesDTO } from '../../models/driver-changes-dto';
import { DriverInfoChangesService } from '../../services/driver-info-changes.service';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-driver-changes-notification',
  templateUrl: './driver-changes-notification.component.html',
  styleUrls: ['./driver-changes-notification.component.css'],
})
export class DriverChangesNotificationComponent implements OnInit {
  @Input() driverChangeInfo: DriverChangesDTO | undefined;
  firstname?: string = '';
  surname?: string = '';
  time?: string = '';
  constructor(
    private driverInfoChangesService: DriverInfoChangesService,
    private adminService: AdminService
  ) {}

  ngOnInit(): void {
    if (this.driverChangeInfo) {
      this.firstname = this.driverChangeInfo.name;
      this.surname = this.driverChangeInfo.surname;
      this.time =
        this.driverChangeInfo.time.split('T')[0] +
        ' ' +
        this.driverChangeInfo.time.split('T')[1].split('.')[0];
    }
  }

  @HostListener('click')
  clicked() {
    this.adminService.setNotificationForm('driver-changes');
    this.driverInfoChangesService.setProduct(this.driverChangeInfo);
  }
}
