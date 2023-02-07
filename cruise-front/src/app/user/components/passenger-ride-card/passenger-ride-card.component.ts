import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { RideDTO } from '../../models/ride-dto';
import { FavouriteService } from '../../services/favourite.service';
import { HistoryService } from '../../services/history.service';
import { FavouriteRideBasicDTO } from '../../models/favourite-ride-basic-dto';
import { FavouriteRideDTO } from '../../models/favourite-ride-dto';
import { Subscription } from 'rxjs';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

@Component({
  selector: 'app-passenger-ride-card',
  templateUrl: './passenger-ride-card.component.html',
  styleUrls: ['./passenger-ride-card.component.css'],
})
export class PassengerRideCardComponent implements OnInit, OnDestroy {
  @Input()
  ride?: RideDTO;
  startLocationAddress?: string = '';
  endLocationAddress?: string = '';
  rideName?: string = '';
  startTime?: string = '';
  endTime?: string = '';
  price?: number = 0;
  distance?: number = 0;
  vehicleType?: string = '';
  numberOfPassengers?: number = 0;
  favouriteRide?: FavouriteRideDTO | null = null;
  favouriteRideSubscription: Subscription = new Subscription();

  constructor(
    private historyService: HistoryService,
    private favouriteService: FavouriteService,
    private popUpService: PopUpService
  ) {}
  ngOnInit(): void {
    this.initialiseValues();
    this.favouriteRideSubscription =
      this.favouriteService.allFavouriteRides$.subscribe((newFavourites) => {
        if (newFavourites != null) {
          this.favouriteRide = null;
          this.checkIfRideIsFavourite(newFavourites);
        }
      });
  }

  ngOnDestroy(): void {
    this.favouriteRideSubscription.unsubscribe();
  }

  initialiseValues() {
    this.startLocationAddress = this.ride?.locations[0].departure.address;
    this.endLocationAddress = this.ride?.locations[0].destination.address;
    this.rideName = `from ${this.startLocationAddress} to ${this.endLocationAddress}`;

    const rideDateTimeStart = this.ride?.startTime.split('.')[0].split('T');
    const rideDateStart = rideDateTimeStart![0].split('-');
    const rideTimeStart = rideDateTimeStart![1];

    const rideDateTimeEnd = this.ride?.endTime.split('.')[0].split('T');
    const rideDateEnd = rideDateTimeEnd![0].split('-');
    const rideTimeEnd = rideDateTimeEnd![1];

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

    this.price = this.ride?.totalCost;
    this.distance = this.ride?.distance;
    this.vehicleType = this.ride?.vehicleType.toLocaleLowerCase();
    this.numberOfPassengers = this.ride?.passengers.length;
  }

  checkIfRideIsFavourite(newFavourites: FavouriteRideDTO[]) {
    if (newFavourites == null) {
      return;
    }
    if (this.ride == null) {
      return;
    }
    for (const fRide of newFavourites) {
      if (this.areRidesEqual(this.ride, fRide)) {
        this.favouriteRide = fRide;
        this.rideName = this.favouriteRide.favoriteName;
      }
    }
  }

  areRidesEqual(ride: RideDTO, favouriteRide: FavouriteRideDTO): boolean {
    if (ride.vehicleType != favouriteRide.vehicleType) return false;
    if (ride.babyTransport != favouriteRide.babyTransport) return false;
    if (ride.petTransport != ride.petTransport) return false;
    for (const passenger of ride.passengers) {
      let found = false;
      for (const fpassenger of favouriteRide.passengers) {
        if (passenger.email == fpassenger.email) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    for (const location of ride.locations) {
      for (const flocation of favouriteRide.locations) {
        if (
          location.departure.address != flocation.departure.address ||
          location.destination.address != location.destination.address
        )
          return false;
      }
    }
    return true;
  }

  rideSelected() {
    this.historyService.selectNewRide(this.ride!);
  }

  markAsFavouriteRide() {
    if (this.favouriteRide == null) {
      const favouriteRideBasicDTO = <FavouriteRideBasicDTO>{
        favoriteName: `${this.startLocationAddress} - ${this.endLocationAddress}`,
        locations: this.ride?.locations,
        passengers: this.ride?.passengers,
        vehicleType: this.ride?.vehicleType,
        babyTransport: this.ride?.babyTransport,
        petTransport: this.ride?.petTransport,
        distance: this.ride?.distance || 100,
      };
      this.favouriteService
        .addRideToFavourites(favouriteRideBasicDTO)
        .subscribe({
          next: (response) => {
            this.popUpService.showPopUp('Ride added to favourites');
            this.favouriteRide = response;
            this.favouriteService.addNewFavouriteRide(this.favouriteRide);
          },
          error: (err) => {
            this.popUpService.showPopUp('Error not added to favourites');
          },
        });
    }
  }
  unmarkAsFavouriteRide() {
    if (this.favouriteRide != null) {
      this.favouriteService
        .removeRideFromFavourites(this.favouriteRide.id)
        .subscribe({
          next: (response) => {
            this.popUpService.showPopUp('Ride deleted from favourites');
            if (this.favouriteRide) {
              this.favouriteService.removeFavouriteRide(this.favouriteRide);
            }
            this.favouriteRide = response;
          },
          error: (err) => {
            this.popUpService.showPopUp('Error not deleted to favourites');
          },
        });
    }
  }
}
