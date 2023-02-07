import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';

import { AuthService } from 'src/app/auth/services/auth.service';
import { StarRatingColor } from 'src/app/universal-components/components/star-rating/star-rating.component';
import { MapService } from 'src/app/universal-components/services/map.service';
import { LocationPairDTO } from 'src/app/unregistered-user/models/location-pair-dto';
import { RejectionDTO } from '../../models/rejection-dto';
import { ReviewDTO } from '../../models/review-dto';
import { RideDTO } from '../../models/ride-dto';
import { UserForRideDTO } from '../../models/user-for-ride-dto';
import { HistoryService } from '../../services/history.service';
import { ReviewService } from '../../services/review.service';
import { RideRequestService } from '../../services/ride-request.service';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

@Component({
  selector: 'app-passenger-detailed-history',
  templateUrl: './passenger-detailed-history.component.html',
  styleUrls: ['./passenger-detailed-history.component.css'],
})
export class PassengerDetailedHistoryComponent implements OnInit, OnDestroy {
  constructor(
    private historyService: HistoryService,
    private reviewService: ReviewService,
    private authService: AuthService,
    private mapService: MapService,
    private rideRequestService: RideRequestService,
    private router: Router,
    private popUpService: PopUpService
  ) {}

  userId: number = -1;

  vehicleRating = 0;
  driverRating = 0;
  starCount = 5;
  starColor = StarRatingColor.accent;
  starColorP = StarRatingColor.primary;
  starColorW = StarRatingColor.warn;
  driverReviewAlredayAssigned = false;
  vehicleReviewAlredayAssigned = false;

  rideId: number = -1;
  rideDetails: RideDTO | null = null;
  startLocationAddress: string = '';
  endLocationAddress: string = '';
  startTime: string = '';
  endTime: string = '';
  totalCost: number = 0;
  vehicleType: string = '';
  numberOfPassengers: number = 0;
  additionalPassengers: UserForRideDTO[] = [];
  duration: number = 0;
  babyTransport: string = '';
  petTransport: string = '';
  status: string = '';
  rejection?: RejectionDTO | null;
  driver?: UserForRideDTO | null;

  historySubscription?: Subscription;

  driverReviewForm = new FormGroup({
    commentDriver: new FormControl('', Validators.required),
  });

  vehicleReviewForm = new FormGroup({
    commentVehicle: new FormControl('', Validators.required),
  });

  ngOnInit(): void {
    this.userId = this.authService.getId();
    this.historySubscription = this.historyService.selectedRide$.subscribe(
      (newRide) => {
        if (newRide !== null) {
          this.showSelectedRide(newRide!);
          this.getAllRideReviews(this.rideId);
          this.showRouteOnMap(newRide.locations);
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.historySubscription?.unsubscribe();
  }

  showRouteOnMap(locations: LocationPairDTO[]) {
    this.mapService.requestRoute(
      {
        lat: locations[0].departure.latitude,
        lng: locations[0].departure.longitude,
      },
      {
        lat: locations[0].destination.latitude,
        lng: locations[0].destination.longitude,
      }
    );
  }

  getAllRideReviews(rideIdToUse: number) {
    this.driverReviewAlredayAssigned = false;
    this.vehicleReviewAlredayAssigned = false;
    this.vehicleRating = 0;
    this.driverRating = 0;
    this.reviewService
      .getAllUserReviewsForRide(rideIdToUse)
      .subscribe((reviewPair) => {
        for (let review of reviewPair) {
          if (review.driverReview.passenger.id != this.userId) {
            continue;
          }
          if (review.driverReview != null) {
            this.driverReviewAlredayAssigned = true;
            this.driverRating = review.driverReview.rating;
          }
          if (review.vehicleReview != null) {
            this.vehicleReviewAlredayAssigned = true;
            this.vehicleRating = review.vehicleReview.rating;
          }
        }
      });
  }

  showSelectedRide(ride: RideDTO) {
    this.rideId = ride.id;
    this.rideDetails = ride;
    this.startLocationAddress = this.rideDetails.locations[0].departure.address;
    this.endLocationAddress = this.rideDetails.locations[0].destination.address;
    this.rejection = this.rideDetails.rejection;
    this.totalCost = this.rideDetails.totalCost;
    this.vehicleType = this.rideDetails.vehicleType.toLocaleLowerCase();
    this.status = this.rideDetails.status;
    this.driver = this.rideDetails.driver;
    this.numberOfPassengers = this.rideDetails.passengers.length;
    for (let passenger of this.rideDetails.passengers) {
      if (passenger.id != this.userId) {
        this.additionalPassengers.push(passenger);
      }
    }

    const rideDateTimeStart = this.rideDetails?.startTime
      .split('.')[0]
      .split('T');
    const rideDateStart = rideDateTimeStart![0].split('-');
    const rideTimeStart = rideDateTimeStart![1];

    const rideDateTimeEnd = this.rideDetails?.endTime.split('.')[0].split('T');
    const rideDateEnd = rideDateTimeEnd![0].split('-');
    const rideTimeEnd = rideDateTimeEnd![1];

    this.duration = this.calculateDurationBetweenTwoDates(
      rideDateStart,
      rideTimeStart.split(':'),
      rideDateEnd,
      rideTimeEnd.split(':')
    );

    this.babyTransport = this.rideDetails.babyTransport ? 'used' : 'not used';
    this.petTransport = this.rideDetails.petTransport ? 'used' : 'not used';

    this.startTime = `${(+rideDateStart[2]!).toLocaleString('en-US', {
      minimumIntegerDigits: 2,
      useGrouping: false,
    })}.${(+rideDateStart[1]!).toLocaleString('en-US', {
      minimumIntegerDigits: 2,
      useGrouping: false,
    })}.${rideDateStart[0]} ${rideTimeStart}`;

    this.endTime = `${(+rideDateEnd[2]!).toLocaleString('en-US', {
      minimumIntegerDigits: 2,
      useGrouping: false,
    })}.${(+rideDateEnd[1]!).toLocaleString('en-US', {
      minimumIntegerDigits: 2,
      useGrouping: false,
    })}.${rideDateEnd[0]} ${rideTimeEnd}`;
  }

  driverRatingChanged(rating: number) {
    this.driverRating = rating;
  }
  vehicleRatingChanged(rating: number) {
    this.vehicleRating = rating;
  }

  sumbitDriverReview() {
    if (!this.driverReviewForm.valid || this.driverRating === 0) {
      this.popUpService.showPopUp('You must leave review and comment');
      return;
    }
    if (this.driverReviewForm.value.commentDriver!.length < 5) {
      this.popUpService.showPopUp('Comment must be at leat 5 characters long');
      return;
    }
    const review = <ReviewDTO>{
      rating: this.driverRating,
      comment: this.driverReviewForm.value.commentDriver,
    };
    this.reviewService.leaveReviewForDriver(review, this.rideId).subscribe({
      next: (response) => {
        this.popUpService.showPopUp('Driver successfully reviewed');
        this.getAllRideReviews(this.rideId);
      },
      error: (error: HttpErrorResponse) => {
        this.driverReviewForm.reset();
        this.popUpService.showPopUp(error.error.message);
      },
    });
  }

  sumbitVehicleReview() {
    if (!this.vehicleReviewForm.valid || this.vehicleRating === 0) {
      this.popUpService.showPopUp('You must leave review and comment');
      return;
    }
    if (this.vehicleReviewForm.value.commentVehicle!.length < 5) {
      this.popUpService.showPopUp('Comment must be at leat 5 characters long');
      return;
    }
    const review = <ReviewDTO>{
      rating: this.vehicleRating,
      comment: this.vehicleReviewForm.value.commentVehicle,
    };
    this.reviewService.leaveReviewForvehicle(review, this.rideId).subscribe({
      next: (response) => {
        this.popUpService.showPopUp('Vehicle successfully reviewed');
        this.getAllRideReviews(this.rideId);
      },
      error: (error: HttpErrorResponse) => {
        this.vehicleReviewForm.reset();
        this.popUpService.showPopUp(error.error.message);
      },
    });
  }

  getRequest(): RideDTO {
    return <RideDTO>{
      id: -1,
      passengers: this.rideDetails?.passengers,
      locations: this.rideDetails?.locations,
      vehicleType: this.rideDetails?.vehicleType,
      babyTransport: this.rideDetails?.babyTransport,
      petTransport: this.rideDetails?.petTransport,
      estimatedTimeInMinutes: this.rideDetails?.estimatedTimeInMinutes,
      totalCost: this.rideDetails?.estimatedTimeInMinutes,
      startTime: new Date().toISOString(),
      endTime: new Date().toISOString(),
      distance: this.rideDetails?.distance,
      rejection: null,
      driver: null,
      status: '',
    };
  }

  requestHistoryRide() {
    this.rideRequestService.setRide(this.getRequest());
    this.router.navigateByUrl('/ride-confirmation');
  }

  calculateDurationBetweenTwoDates(
    startDate: string[],
    startTime: string[],
    endDate: string[],
    endTime: string[]
  ): number {
    const startInMinutes =
      +startDate[0] * 525600 +
      +startDate[1] * 43800 +
      +startDate[2] * 1440 +
      +startTime[0] * 60 +
      +startTime[1];

    const endInMinutes =
      +endDate[0] * 525600 +
      +endDate[1] * 43800 +
      +endDate[2] * 1440 +
      +endTime[0] * 60 +
      +endTime[1];

    return endInMinutes - startInMinutes;
  }
}
