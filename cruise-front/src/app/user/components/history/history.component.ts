import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { Observable, Subscription } from 'rxjs';

import { RideDTO } from '../../models/ride-dto';
import { DriverService } from '../../../driver/services/driver.service';
import { AuthService } from '../../../auth/services/auth.service';
import { UserService } from '../../services/user.service';
import { RideService } from '../../services/ride.service';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css'],
})
export class HistoryComponent implements OnInit, OnDestroy {
  rides: RideDTO[] = [];
  obs: Observable<any> | undefined;
  dataSource!: MatTableDataSource<RideDTO>;
  displayedColumns: string[] = [' '];

  @ViewChild(MatPaginator) paginator!: any;
  who!: string;
  userToShow!: string;

  formGroup = new FormGroup({
    from: new FormControl(''),
    to: new FormControl(''),
    sort: new FormControl(''),
  });

  constructor(
    private driverService: DriverService,
    private authService: AuthService,
    private userService: UserService,
    private rideService: RideService
  ) {}

  ngOnInit(): void {
    if (this.authService.getRole() === 'ROLE_DRIVER') {
      this.who = 'DRIVER';
      this.userToShow = 'DRIVER';
    } else if (this.authService.getRole() === 'ROLE_ADMIN') {
      this.who = 'ADMIN';
    }
  }

  getRidesSubscription: Subscription = new Subscription();
  getUserSubscription: Subscription = new Subscription();

  findRides() {
    const dateFrom = this.formGroup.value.from!;
    const dateTo = this.formGroup.value.to!;
    const sortWay = this.formGroup.value.sort!;

    if (this.authService.getRole() === 'ROLE_DRIVER')
      this.getRidesSubscription = this.driverService
        .getAllRides(this.authService.getId(), -1, 1, sortWay, dateFrom, dateTo)
        .subscribe((response) => {
          this.rides = response.results;
          this.dataSource = new MatTableDataSource<RideDTO>(this.rides);
          this.dataSource.paginator = this.paginator;
        });
    else if (this.authService.getRole() === 'ROLE_ADMIN') {
      this.getUserSubscription = this.userService
        .getUserByEmail(
          (<HTMLInputElement>document.getElementById('useremail')).value
        )
        .subscribe((user) => {
          this.getRidesSubscription = this.rideService
            .getAllUserRides(user.id)
            .subscribe(
              (rides) => {
                this.rides = rides.results;
                this.userToShow = 'PASSENGER';
                this.dataSource = new MatTableDataSource<RideDTO>(this.rides);
                this.dataSource.paginator = this.paginator;
              },
              () => {
                this.driverService
                  .getAllRides(user.id, -1, 1, sortWay, dateFrom, dateTo)
                  .subscribe((response) => {
                    this.userToShow = 'DRIVER';
                    this.rides = response.results;
                    this.dataSource = new MatTableDataSource<RideDTO>(
                      this.rides
                    );
                    this.dataSource.paginator = this.paginator;
                  });
              }
            );
        });
    }
  }

  ngOnDestroy(): void {
    this.getRidesSubscription.unsubscribe();
    this.getUserSubscription.unsubscribe();
  }
}
