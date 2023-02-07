import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { lastValueFrom, Subscription } from 'rxjs';

import { RideEstimationService } from '../../services/ride-estimation.service';
import { RideInfoDTO } from '../../models/ride-basic-info-dto';
import { LocationDTO } from '../../models/location-dto';
import { OfferDTO } from '../../models/offer-dto';
import { MapService } from 'src/app/universal-components/services/map.service';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

@Component({
  selector: 'app-ride-estimation',
  templateUrl: './ride-estimation.component.html',
  styleUrls: ['./ride-estimation.component.css'],
})
export class RideEstimationComponent implements OnInit, OnDestroy {
  pinSubscription?: Subscription;
  pinOrderedNumber: number = 2;

  constructor(
    private service: RideEstimationService,
    private mapService: MapService,
    private popUpService: PopUpService
  ) {}

  ngOnInit(): void {
    this.pinSubscription = this.mapService.placedPins$.subscribe((address) => {
      if (this.pinOrderedNumber === 1) {
        this.rideEstimationForm.patchValue({
          departure: address,
        });
        this.pinOrderedNumber = 2;
      } else {
        this.rideEstimationForm.patchValue({
          destination: address,
        });
        this.pinOrderedNumber = 1;
      }
    });
  }

  ngOnDestroy(): void {
    this.pinSubscription?.unsubscribe();
  }

  rideEstimationForm = new FormGroup({
    departure: new FormControl('', [
      Validators.required,
      Validators.minLength(1),
    ]),
    destination: new FormControl('', [
      Validators.required,
      Validators.minLength(1),
    ]),
  });

  offers: OfferDTO[] = [];

  showNeedToRegisterPopUp() {
    this.popUpService.showPopUp(
      'You need to register to be able to book a ride'
    );
  }

  async getEstimation() {
    if (!this.rideEstimationForm.valid) {
      this.popUpService.showPopUp('Departure and destinatio are required');
      return;
    }

    const departure: LocationDTO | null = await this.getLocation(
      this.rideEstimationForm.value.departure!
    );

    if (departure == null) {
      this.popUpService.showPopUp('Departure address is not valid!');
      return;
    }

    const destination: LocationDTO | null = await this.getLocation(
      this.rideEstimationForm.value.destination!
    );

    if (destination == null) {
      this.popUpService.showPopUp('Destination address is not valid!');
      return;
    }

    const rideInfo: RideInfoDTO = {
      locations: [{ departure: departure, destination: destination }],
      vehicleType: 'STANDARD',
      babyTransport: false,
      petTransport: false,
    };

    this.service.requestRideEstimation(rideInfo).subscribe((offer) => {
      this.offers = [offer];
      this.mapService.requestRoute(
        { lat: departure!.latitude, lng: departure!.longitude },
        { lat: destination!.latitude, lng: destination!.longitude }
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
}
