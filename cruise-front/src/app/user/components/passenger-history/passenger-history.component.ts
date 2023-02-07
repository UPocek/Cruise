import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/auth/services/auth.service';
import { RideDTO } from '../../models/ride-dto';
import { FavouriteService } from '../../services/favourite.service';
import { RideService } from '../../services/ride.service';

@Component({
  selector: 'app-passenger-history',
  templateUrl: './passenger-history.component.html',
  styleUrls: ['./passenger-history.component.css'],
})
export class PassengerHistoryComponent implements OnInit, OnDestroy {
  userId = -1;
  allRides?: RideDTO[];
  userRidesSubscription: Subscription = new Subscription();
  favouriteRidesSubscription: Subscription = new Subscription();

  formFilters = new FormGroup({
    from: new FormControl(''),
    to: new FormControl(''),
    sort: new FormControl(''),
  });

  constructor(
    private rideService: RideService,
    private favouriteservice: FavouriteService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.userId = this.auth.getId();
    this.getAllUserRides();
    this.getAllFavouriteRides();
  }

  ngOnDestroy(): void {
    this.userRidesSubscription.unsubscribe();
    this.favouriteRidesSubscription.unsubscribe();
  }

  getAllUserRides(from: string = '', to: string = '', sort: string = '') {
    this.userRidesSubscription = this.rideService
      .getAllUserRides(this.userId, from, to, sort)
      .subscribe((rides) => {
        this.allRides = rides.results;
      });
  }

  getAllFavouriteRides() {
    this.favouriteRidesSubscription = this.favouriteservice
      .getAllUserFavouriteRides()
      .subscribe((rides) => {
        this.favouriteservice.setFavouriteRides(rides);
      });
  }

  filterHistory() {
    if (this.formFilters.valid && this.isAtLeastOneFilterSet()) {
      const dateFrom = this.formFilters.value.from!;
      const dateTo = this.formFilters.value.to!;
      const sortWay = this.formFilters.value.sort!;

      this.getAllUserRides(dateFrom, dateTo, sortWay);
    }
  }

  isAtLeastOneFilterSet(): boolean {
    return (
      this.formFilters.value.from != '' ||
      this.formFilters.value.to != '' ||
      this.formFilters.value.sort != ''
    );
  }
}
