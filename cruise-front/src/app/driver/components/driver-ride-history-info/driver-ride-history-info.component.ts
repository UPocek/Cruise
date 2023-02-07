import { Component, OnDestroy, OnInit } from '@angular/core';
import { DriverService } from '../../services/driver.service';
import { RideDTO } from '../../../user/models/ride-dto';
import { patchTsGetExpandoInitializer } from '@angular/compiler-cli/ngcc/src/packages/patch_ts_expando_initializer';
import { UserDTO } from '../../../user/models/user-dto';
import { UserForRideDTO } from '../../../user/models/user-for-ride-dto';
import { StarRatingColor } from '../../../universal-components/components/star-rating/star-rating.component';
import { ReviewService } from '../../../user/services/review.service';
import { isElementScrolledOutsideView } from '@angular/cdk/overlay/position/scroll-clip';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-driver-ride-history-info',
  templateUrl: './driver-ride-history-info.component.html',
  styleUrls: ['./driver-ride-history-info.component.css'],
})
export class DriverRideHistoryInfoComponent implements OnInit, OnDestroy {
  ride!: RideDTO;
  dateTo!: string;
  dateFrom!: string;
  departure!: string;
  destination!: string;
  time!: number;
  distance!: number;
  price!: number;
  passengers: UserForRideDTO[] = [];
  vehicleRating: number = 0;
  driverRating: number = 0;
  starColor = StarRatingColor.accent;
  historyRideSubscribtion: Subscription = new Subscription();
  rideReviewSubscription: Subscription = new Subscription();
  constructor(
    private driverService: DriverService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    this.historyRideSubscribtion = this.driverService.historyRide.subscribe(
      (ride) => {
        this.ride = ride;
        this.dateFrom =
          this.ride.startTime.split('T')[0] +
          ' ' +
          this.ride.startTime.split('T')[1].split('.')[0];
        this.dateTo =
          this.ride.endTime.split('T')[0] +
          ' ' +
          this.ride.endTime.split('T')[1].split('.')[0];
        this.departure = this.ride.locations[0].departure.address;
        this.destination = this.ride.locations[0].destination.address;
        this.passengers = this.ride.passengers;
        this.time = this.ride.estimatedTimeInMinutes;
        this.distance = this.ride.distance;
        this.price = this.ride.totalCost;

        this.historyRideSubscribtion = this.reviewService
          .getAllUserReviewsForRide(this.ride!.id)
          .subscribe((reviews) => {
            let i = 0;
            let dr = 0;
            let vr = 0;
            let input = document.getElementById('comments')!;
            input.innerText = ""
            this.vehicleRating = 0;
            this.driverRating = 0;
            for (i = 0; i < reviews.length; i++) {
              if (reviews[i].vehicleReview != null) {
                input.innerText +=
                  'VEHICLE: ' + reviews[i].vehicleReview.comment + '\n';
                this.vehicleRating += reviews[i].vehicleReview.rating;
                vr++;
              }
              if (reviews[i].driverReview != null) {
                input.innerText +=
                  'DRIVER: ' + reviews[i].driverReview.comment + '\n';
                this.driverRating += reviews[i].driverReview.rating;
                dr++;
              }
            }
            this.driverRating = this.driverRating / dr;
            this.vehicleRating = this.vehicleRating / vr;
          });
      }
    );
  }

  ngOnDestroy() {
    this.historyRideSubscribtion.unsubscribe();
    this.rideReviewSubscription.unsubscribe();
  }
}
