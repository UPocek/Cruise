import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';

import { MapService } from 'src/app/universal-components/services/map.service';
import { LocationDTO } from 'src/app/unregistered-user/models/location-dto';
import { LocationPairDTO } from 'src/app/unregistered-user/models/location-pair-dto';
import { OfferDTO } from 'src/app/unregistered-user/models/offer-dto';
import { RideInfoDTO } from 'src/app/unregistered-user/models/ride-basic-info-dto';
import { RideEstimationService } from 'src/app/unregistered-user/services/ride-estimation.service';
import { EmailDTO } from '../../models/email-dto';
import { RideDTO } from '../../models/ride-dto';
import { RideRequestDTO } from '../../models/ride-request-dto';
import { UserForRideDTO } from '../../models/user-for-ride-dto';
import { RideRequestService } from '../../services/ride-request.service';
import { AuthService } from '../../../auth/services/auth.service';
import { FavouriteRideBasicDTO } from '../../models/favourite-ride-basic-dto';
import { FavouriteService } from '../../services/favourite.service';
import { FavouriteRideDTO } from '../../models/favourite-ride-dto';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-create-ride-request',
  templateUrl: './create-ride-request.component.html',
  styleUrls: ['./create-ride-request.component.css'],
})
export class CreateRideRequestComponent implements OnInit {
  isDetails: boolean = true;
  passengerId!: number;
  inviteRequest: string = '';
  splitFare: string[] = [];
  offers: OfferDTO[] = [];
  departure: LocationDTO = <LocationDTO>{};
  destination: LocationDTO = <LocationDTO>{};
  url: string = 'http://localhost:8080';
  isLoaded = false;
  ride: RideDTO = <RideDTO>{};
  favouriteRides!: FavouriteRideDTO[];

  rideSetToFavourite: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private mapService: MapService,
    private rideEstimationService: RideEstimationService,
    private rideRequestService: RideRequestService,
    private router: Router,
    private authService: AuthService,
    private favouriteService: FavouriteService,
    private popUpService: PopUpService
  ) {}

  rideForm = this.formBuilder.group({
    departure: ['', Validators.required],
    destination: ['', Validators.required],
    time: [new Date().toISOString(), Validators.required],
    pets: [''],
    babys: [''],
    vehicleType: ['', Validators.required],
  });

  ngOnInit(): void {
    this.passengerId = this.authService.getId();
    this.favouriteService.getAllUserFavouriteRides().subscribe((response) => {
      this.favouriteRides = response;
    });
  }

  addFriend(friendEmail: string): void {
    this.inviteRequest = friendEmail;
  }

  inviteFriend(): void {
    this.splitFare.push(this.inviteRequest);
    this.inviteRequest = '';
  }

  requestRide(offerIndex: number) {
    let nowPlusFifteenMinutes = new Date(new Date().getTime() + 15 * 60000);
    if (
      new Date(this.rideForm.value.time!) <
      new Date(new Date().getTime() - 5 * 60000)
    ) {
      this.popUpService.showPopUp('Entered time not valid.');
    } else if (
      this.splitFare.length > 0 &&
      new Date(this.rideForm.value.time!) > nowPlusFifteenMinutes
    ) {
      let ride = this.getRequestPendingOrFuture(offerIndex);
      this.rideRequestService.sendRideRequest(ride).subscribe({
        next: (ride) => {
          this.ride = ride;
          this.rideRequestService.setRide(ride);
          return this.rideRequestService
            .sendRideInvitations(<EmailDTO>{
              rideId: ride.id,
              emails: this.splitFare,
              invitingPassenger: this.passengerId,
            })
            .subscribe({
              next: (response) => {
                this.popUpService.showPopUp(
                  'You succesfully scheduled future ride. \n We will cruise soon :)'
                );
              },
              error: () => {
                this.popUpService.showPopUp(
                  "Can't send invitation because passenger is already in ride process"
                );
                this.router.navigateByUrl('/ride-confirmation');
              },
            });
        },
        error: () => {
          this.popUpService.showPopUp('You already have ride in process');
        },
      });
    } else if (new Date(this.rideForm.value.time!) > nowPlusFifteenMinutes) {
      let ride = this.getRequestPendingOrFuture(offerIndex);
      this.rideRequestService.sendRideRequest(ride).subscribe({
        next: (ride) => {
          this.ride = ride;
          this.rideRequestService.setRide(ride);
          this.popUpService.showPopUp(
            'You succesfully scheduled future ride. \n We will cruise soon :)'
          );
        },
        error: () => {
          this.popUpService.showPopUp(
            "You can't create a ride because already have ride in process"
          );
        },
      });
    } else if (this.splitFare.length > 0) {
      let ride = this.getRequestPendingOrFuture(offerIndex);
      this.rideRequestService.sendRideRequest(ride).subscribe({
        next: (ride) => {
          this.ride = ride;

          this.rideRequestService.setRide(ride);
          return this.rideRequestService
            .sendRideInvitations(<EmailDTO>{
              rideId: ride.id,
              emails: this.splitFare,
              invitingPassenger: this.passengerId,
            })
            .subscribe({
              next: (response) => {
                this.rideRequestService.setEmails(this.splitFare);
                this.router.navigateByUrl('/email-response');
              },
              error: () => {
                this.popUpService.showPopUp(
                  "Can't send invitation because passenger is already in ride process"
                );
                this.router.navigateByUrl('/ride-confirmation');
              },
            });
        },
        error: () => {
          this.popUpService.showPopUp(
            'Hey cruiser, you already have ride in process'
          );
        },
      });
    } else {
      let ride = this.getRequest(offerIndex);
      this.rideRequestService.setRide(ride);
      this.router.navigateByUrl('/ride-confirmation');
    }
  }

  getRequest(offerIndex: number): RideDTO {
    return <RideDTO>{
      id: -1,
      passengers: [
        <UserForRideDTO>{
          id: this.passengerId,
          email: this.authService.getEmail(),
        },
      ],
      locations: [
        <LocationPairDTO>{
          departure: this.departure,
          destination: this.destination,
        },
      ],
      vehicleType: this.rideForm.value.vehicleType,
      babyTransport: this.rideForm.value.babys == 'true',
      petTransport: this.rideForm.value.pets == 'true',
      estimatedTimeInMinutes: this.offers[offerIndex].estimatedTimeInMinutes,
      totalCost: this.offers[offerIndex].estimatedCost,
      startTime: this.rideForm.value.time,
      endTime: this.rideForm.value.time,
      distance: this.offers[offerIndex].distance,
      rejection: null,
      driver: null,
      status: '',
    };
  }

  getRequestPendingOrFuture(offerIndex: number): RideRequestDTO {
    return <RideRequestDTO>{
      passengers: [
        <UserForRideDTO>{
          id: this.passengerId,
          email: this.authService.getEmail(),
        },
      ],
      locations: [
        <LocationPairDTO>{
          departure: this.departure,
          destination: this.destination,
        },
      ],
      vehicleType: this.rideForm.value.vehicleType,
      babyTransport: this.rideForm.value.babys == 'true' ? true : false,
      petTransport: this.rideForm.value.pets == 'true' ? true : false,
      timeEstimation: this.offers[offerIndex].estimatedTimeInMinutes,
      price: this.offers[offerIndex].estimatedCost,
      startTime: this.rideForm.value.time,
      distance: this.offers[offerIndex].distance,
    };
  }

  async getOffers() {
    if (!this.rideForm.valid) {
      this.popUpService.showPopUp('Departure and destination are required');
      return;
    }

    let departureLocation = await this.getLocation(
      this.rideForm.value.departure!
    );

    if (departureLocation == null) {
      this.popUpService.showPopUp('Departure address is not valid!');
      return;
    }

    let destinationLocation = await this.getLocation(
      this.rideForm.value.destination!
    );

    if (destinationLocation == null) {
      this.popUpService.showPopUp('Destination address is not valid!');
      return;
    }

    this.departure = departureLocation;
    this.destination = destinationLocation;

    let rideInfo = <RideInfoDTO>{
      locations: [
        {
          departure: this.departure,
          destination: this.destination,
        },
      ],
      vehicleType: this.rideForm.value.vehicleType,
      babyTransport: this.rideForm.value.babys == 'true',
      petTransport: this.rideForm.value.pets == 'true',
    };

    this.rideEstimationService
      .requestRideEstimation(rideInfo)
      .subscribe((offer) => {
        this.offers = [offer];
        this.mapService.requestRoute(
          { lat: this.departure.latitude, lng: this.departure.longitude },
          { lat: this.destination.latitude, lng: this.destination.longitude }
        );
      });
  }

  async getLocation(addressName: string): Promise<LocationDTO | null> {
    const result$ = this.mapService.getLatLngFromAddress(addressName);
    const completeLocation = await lastValueFrom(result$);
    if (completeLocation.status === 'OK') {
      const latLngLocation =
        completeLocation['results'][0]['geometry']['location'];
      return {
        address: addressName,
        latitude: latLngLocation.lat,
        longitude: latLngLocation.lng,
      };
    }
    return null;
  }

  setIsDetails(state: boolean) {
    this.isDetails = state;
  }

  async markAsFavouriteRide() {
    if (this.rideForm.valid) {
      this.rideSetToFavourite = !this.rideSetToFavourite;
      const locationDeparture = await this.getLocation(
        this.rideForm.value.departure!
      );
      const locationDestination = await this.getLocation(
        this.rideForm.value.destination!
      );

      let passengersList: UserForRideDTO[] = [];
      for (let friendEmail of this.splitFare) {
        passengersList.push({ id: -1, email: friendEmail });
      }

      const favouriteRideBasicDTO = {
        favoriteName: `${this.rideForm.value.departure} - ${this.rideForm.value.destination}`,
        locations: [
          {
            departure: locationDeparture,
            destination: locationDestination,
          },
        ],
        passengers: passengersList,
        vehicleType: this.rideForm.value.vehicleType,
        babyTransport: this.rideForm.value.babys != '',
        petTransport: this.rideForm.value.pets != '',
        distance: this.offers[0].distance || 0,
      } as FavouriteRideBasicDTO;

      this.favouriteService
        .addRideToFavourites(favouriteRideBasicDTO)
        .subscribe({
          next: (response) => {
            this.popUpService.showPopUp('Ride added to favourites');
          },
          error: (error: HttpErrorResponse) => {
            this.popUpService.showPopUp(error.error.message);
          },
        });
    } else {
      this.popUpService.showPopUp('First enter all required fields into form');
    }
  }

  getFavouriteRideRequest(
    estimatedTimeInMinutesOffer: number,
    totalCostOffer: number,
    distanceOffer: number,
    favouriteRideIndex: number
  ) {
    return <RideDTO>{
      id: -1,
      passengers: this.favouriteRides[favouriteRideIndex].passengers,
      locations: this.favouriteRides[favouriteRideIndex].locations,
      vehicleType: this.favouriteRides[favouriteRideIndex].vehicleType,
      babyTransport: this.favouriteRides[favouriteRideIndex].babyTransport,
      petTransport: this.favouriteRides[favouriteRideIndex].petTransport,
      estimatedTimeInMinutes: estimatedTimeInMinutesOffer,
      totalCost: totalCostOffer,
      startTime: new Date().toISOString(),
      endTime: new Date().toISOString(),
      distance: distanceOffer,
      rejection: null,
      driver: null,
      status: '',
    };
  }

  getFavouriteRideInfo(favouriteRideIndex: number): RideInfoDTO {
    return <RideInfoDTO>{
      locations: [
        {
          departure:
            this.favouriteRides[favouriteRideIndex].locations[0].departure,
          destination:
            this.favouriteRides[favouriteRideIndex].locations[0].destination,
        },
      ],
      vehicleType: this.favouriteRides[favouriteRideIndex].vehicleType,
      babyTransport: this.favouriteRides[favouriteRideIndex].babyTransport,
      petTransport: this.favouriteRides[favouriteRideIndex].petTransport,
    };
  }

  requestFavouriteRide(favouriteRideIndex: number) {
    let rideInfo = this.getFavouriteRideInfo(favouriteRideIndex);
    this.rideEstimationService
      .requestRideEstimation(rideInfo)
      .subscribe((offer) => {
        let ride = this.getFavouriteRideRequest(
          offer.estimatedTimeInMinutes,
          offer.estimatedCost,
          offer.distance,
          favouriteRideIndex
        );
        this.rideRequestService.setRide(ride);
        this.router.navigateByUrl('/ride-confirmation');
      });
  }
}
