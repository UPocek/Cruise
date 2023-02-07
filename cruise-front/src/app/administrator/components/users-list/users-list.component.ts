import {
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  ViewChildren,
} from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, Subscription } from 'rxjs';

import { UserInfoService } from '../../services/user-info.service';
import { UserDTO } from '../../../user/models/user-dto';

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.css'],
})
export class UsersListComponent implements OnInit, OnDestroy {
  passengerList: UserDTO[] = [];
  driverList: UserDTO[] = [];
  obs: Observable<any> | undefined;
  dataSource!: MatTableDataSource<UserDTO>;
  dataSourceDriver!: MatTableDataSource<UserDTO>;
  displayedColumns: string[] = ['passenger'];
  displayedColumnsDriver: string[] = ['driver'];

  passengersSubscription: Subscription = new Subscription();
  driverSubscription: Subscription = new Subscription();

  @ViewChildren(MatPaginator) paginators!: any;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private userInfoService: UserInfoService
  ) {}

  ngOnDestroy(): void {
    this.driverSubscription.unsubscribe();
    this.passengersSubscription.unsubscribe();
  }

  ngOnInit(): void {
    this.passengersSubscription = this.userInfoService
      .getAllPassengers(-1, 1)
      .subscribe((response) => {
        this.passengerList = response.results;
        this.dataSource = new MatTableDataSource<UserDTO>(this.passengerList);
        this.dataSource.paginator = this.paginators.toArray()[0];
      });

    this.driverSubscription = this.userInfoService
      .getAllDrivers(-1, 1)
      .subscribe((response) => {
        this.driverList = response.results;
        this.dataSourceDriver = new MatTableDataSource<UserDTO>(
          this.driverList
        );
        this.dataSourceDriver.paginator = this.paginators.toArray()[1];
      });
  }
}
