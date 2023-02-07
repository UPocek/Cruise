import { Component, OnDestroy, OnInit } from '@angular/core';
import { map, Observable, Subscription } from 'rxjs';
import { MapDirectionsService } from '@angular/google-maps';
import { Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

import { RejectionDTO } from 'src/app/user/models/rejection-dto';
import { RideDTO } from 'src/app/user/models/ride-dto';
import { FormControl, FormGroup } from '@angular/forms';
import { DriverService } from '../../services/driver.service';
import { MapService } from '../../../universal-components/services/map.service';
import { Router } from '@angular/router';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

@Component({
  selector: 'app-driver-ride-request',
  templateUrl: './driver-ride-request.component.html',
  styleUrls: ['./driver-ride-request.component.css'],
})
export class DriverRideRequestComponent implements OnInit, OnDestroy {
  directionResults$?: Observable<google.maps.DirectionsResult | undefined>;

  ride!: RideDTO;
  departure?: string;
  departureGeoLocation?: google.maps.LatLngLiteral;
  destination?: string;
  destinationGeoLocation?: google.maps.LatLngLiteral;
  estimatedTime?: number;
  distance?: number;
  numberOfPassengers?: number;
  cost?: number;

  url = 'http://localhost:8080';
  isLoaded = false;
  private stompClient: any;

  userId: number = -1;

  reasonForm = new FormGroup({
    reason: new FormControl(''),
  });

  constructor(
    private mapDirectionsService: MapDirectionsService,
    private driverService: DriverService,
    private mapService: MapService,
    private router: Router,
    private popUpService: PopUpService
  ) {}

  ngOnInit(): void {
    this.initializeWebSocketConnection();
    this.driverService.currentDriverRide$.subscribe((ride) => {
      if (ride.id !== undefined) {
        this.handleResult(ride);
      }
    });
  }

  initializeWebSocketConnection() {
    let that = this;

    this.stompClient = Stomp.over(function () {
      return new SockJS(`${that.url}/socket`);
    });

    this.stompClient.connect({}, function () {
      that.isLoaded = true;
    });
  }

  sendResponseMessageUsingSocket(ride: RideDTO) {
    this.stompClient.send('/socket-in/ride-request', {}, JSON.stringify(ride));
  }

  handleResult(ride: RideDTO) {
    this.ride = ride;
    this.departure = ride.locations[0].departure.address;
    this.departureGeoLocation = {
      lat: ride.locations[0].departure.latitude,
      lng: ride.locations[0].departure.longitude,
    };
    this.destination = ride.locations[0].destination.address;
    this.destinationGeoLocation = {
      lat: ride.locations[0].destination.latitude,
      lng: ride.locations[0].destination.longitude,
    };
    this.estimatedTime = ride.estimatedTimeInMinutes;
    this.distance = Math.round(ride.distance / 100) / 10;
    this.numberOfPassengers = ride.passengers.length;
    this.cost = ride.totalCost;
    this.mapService.requestRoute(
      this.departureGeoLocation,
      this.destinationGeoLocation
    );
    // this.displayRoute(this.departureGeoLocation, this.destinationGeoLocation);
  }

  acceptRide(response: boolean) {
    if (response) {
      this.driverService.addRideIdOfNewAnswer(this.ride.id);
      this.sendResponseMessageUsingSocket(this.ride);
      this.router.navigateByUrl('/current-ride');
    } else {
      if (this.reasonForm.value.reason?.trim() == '') {
        this.popUpService.showPopUp(
          'You must enter reason if you want to decline a ride'
        );
      } else {
        this.ride.rejection = <RejectionDTO>{
          reason: this.reasonForm.value.reason,
          timeOfRejection: new Date().toISOString(),
        };
        this.driverService.addRideIdOfNewAnswer(this.ride.id);
        this.sendResponseMessageUsingSocket(this.ride);
        this.router.navigateByUrl('/driver-main');
      }
    }
  }

  displayRoute(
    origin: google.maps.LatLngLiteral,
    destination: google.maps.LatLngLiteral
  ) {
    const request: google.maps.DirectionsRequest = {
      destination: destination,
      origin: origin,
      travelMode: google.maps.TravelMode.DRIVING,
    };
    this.directionResults$ = this.mapDirectionsService
      .route(request)
      .pipe(map((response) => response.result));
  }

  ngOnDestroy(): void {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  }
}
