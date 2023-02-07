import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { DriverService } from 'src/app/driver/services/driver.service';
import { LoginService } from 'src/app/auth/services/login.service';
import { AuthService } from '../../../../auth/services/auth.service';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

@Component({
  selector: 'app-driver-activity',
  templateUrl: './driver-activity.component.html',
  styleUrls: ['./driver-activity.component.css'],
})
export class DriverActivityComponent implements OnInit, OnDestroy {
  activity: string = 'ACTIVE';
  isChecked: boolean = true;
  driverName: string = '';
  driverSurname: string = '';
  workingTime: number = 0;
  workingHours: string = '00';
  workingMinutes: string = '00';
  workingTimeTimer: any;
  minute: number = 60000;

  constructor(
    private driverService: DriverService,
    private authService: AuthService,
    private popUpService: PopUpService
  ) {}

  ngOnInit(): void {
    this.driverService
      .getDriver(this.authService.getEmail())
      .subscribe((response) => {
        this.driverName = response.name;
        this.driverSurname = response.surname;
      });

    this.driverService
      .activateDriver(this.authService.getId(), true)
      .subscribe({
        next: (response) => {
          if (response) {
            this.activity = 'ACTIVE';
            this.isChecked = true;
            this.setDriverWorkingTime();
            return;
          }
          this.activity = 'INACTIVE';
          this.isChecked = false;
        },
        error: (err) => {
          this.popUpService.showPopUp('You have exceeded your working time');
          this.activity = 'INACTIVE';
          this.isChecked = false;
        },
      });
  }

  onToggle(event: any) {
    if (event.checked) {
      this.driverService
        .activateDriver(this.authService.getId(), true)
        .subscribe({
          next: (response) => {
            if (response) {
              this.activity = 'ACTIVE';
              this.isChecked = true;
              this.setDriverWorkingTime();
              return;
            }
            this.activity = 'INACTIVE';
            this.isChecked = false;
          },
          error: (err) => {
            this.popUpService.showPopUp('You have exceeded your working time');
            this.activity = 'INACTIVE';
            this.isChecked = false;
          },
        });
    } else {
      this.driverService.logOutDriver(this.authService.getId());
      this.activity = 'INACTIVE';
      clearInterval(this.workingTimeTimer);
    }
  }

  padTo2Digits(num: number): string {
    return num.toString().padStart(2, '0');
  }

  convertWorkingTimeFromMilis() {
    let seconds = Math.floor(this.workingTime / 1000);
    let minutes = Math.floor(seconds / 60);
    let hours = Math.floor(minutes / 60);

    minutes = minutes % 60;

    this.workingHours = this.padTo2Digits(hours);
    this.workingMinutes = this.padTo2Digits(minutes);
  }

  setDriverWorkingTime() {
    this.driverService
      .getDriversWorkingTime(this.authService.getId())
      .subscribe((response) => {
        this.workingTime = response.duration;
        this.convertWorkingTimeFromMilis();
        this.workingTimeTimer = setInterval(() => {
          this.workingMinutes = this.padTo2Digits(+this.workingMinutes + 1);
          if (+this.workingMinutes === 60) {
            this.workingHours = this.padTo2Digits(+this.workingHours + 1);
            this.workingMinutes = this.padTo2Digits(0);
          }
          if (+this.workingHours === 8) {
            this.driverService.logOutDriver(this.authService.getId());
            this.activity = 'INACTIVE';
            this.isChecked = false;
            this.popUpService.showPopUp('You have exceeded your working time');
          }
        }, this.minute);
      });
  }

  ngOnDestroy(): void {
    if (this.workingTimeTimer) {
      clearInterval(this.workingTimeTimer);
    }
  }
}
