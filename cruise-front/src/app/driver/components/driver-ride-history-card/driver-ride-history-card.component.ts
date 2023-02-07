import {
  Component,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Subscription } from 'rxjs';

import { RideDTO } from '../../../user/models/ride-dto';
import { DriverService } from '../../services/driver.service';
import { ReviewService } from '../../../user/services/review.service';
import { ReviewPairDTO } from '../../../user/models/review-pair-dto';

@Component({
  selector: 'app-driver-ride-history-card',
  templateUrl: './driver-ride-history-card.component.html',
  styleUrls: ['./driver-ride-history-card.component.css'],
})
export class DriverRideHistoryCardComponent implements OnInit, OnDestroy {
  @Input() ride!: RideDTO;
  startTime: string = '';
  endTime: string = '';
  departure: string = '';
  destination: string = '';
  totalCost: number = 0;
  distance: number = 0;
  passengers: number = 0;
  rating: number = 0;
  starColor = '#FFFFFF';

  reviews!: ReviewPairDTO[];

  reviewsSubscription: Subscription = new Subscription();
  constructor(
    private driverService: DriverService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    this.startTime =
      this.ride.startTime.split('T')[0] +
      ' ' +
      this.ride.startTime.split('T')[1].split('.')[0];
    this.endTime =
      this.ride.endTime.split('T')[0] +
      ' ' +
      this.ride.endTime.split('T')[1].split('.')[0];
    this.departure = this.ride.locations[0].departure.address;
    this.destination = this.ride.locations[0].destination.address;
    this.totalCost = this.ride.totalCost;
    this.distance = this.ride.distance;
    this.passengers = this.ride.passengers.length;

    this.reviewsSubscription = this.reviewService
      .getAllUserReviewsForRide(this.ride!.id)
      .subscribe((reviews) => {
        this.reviews = reviews;
        let num = 0;
        for (let i = 0; i < reviews.length; i++) {
          if (reviews[i].driverReview !== null) {
            this.rating += reviews[i].driverReview.rating;
            num++;
          }
          if (reviews[i].vehicleReview !== null) {
            this.rating += reviews[i].vehicleReview.rating;
            num++;
          }
        }
        this.rating = this.rating / num;
      });
  }

  ngOnDestroy() {
    this.reviewsSubscription.unsubscribe();
  }
  @HostListener('click')
  clicked() {
    let el = document.getElementById('driverRideInfo');
    // @ts-ignore
    el.style.visibility = 'visible';
    this.driverService.setHistoryRide(this.ride!);
  }
}
