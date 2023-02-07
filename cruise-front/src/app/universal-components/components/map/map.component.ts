import { Component, OnDestroy, OnInit } from '@angular/core';
import { MapDirectionsService } from '@angular/google-maps';
import { map, Observable, Subscription } from 'rxjs';
import { VehicleDTO } from '../../models/vehicle-dto';
import { MapService } from '../../services/map.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
})
export class MapComponent implements OnInit, OnDestroy {
  display: any;
  center: google.maps.LatLngLiteral = {
    lat: 45.2396,
    lng: 19.8227,
  };
  zoom = 13;
  dataRefreshIntervalInSeconds = 6;
  mapRefreshIntervalInSeconds = 2;
  period = 0;
  i1: any;
  i2: any;
  mapSubscription: Subscription = new Subscription();
  vehiclesSubscription: Subscription = new Subscription();
  directionResults$?: Observable<google.maps.DirectionsResult | undefined>;

  constructor(
    private mapDirectionsService: MapDirectionsService,
    private mapService: MapService
  ) {}

  ngOnInit(): void {
    this.getUserLocation();
    this.setUpMap();

    this.mapSubscription = this.mapService.route$.subscribe((route) => {
      if (route != null) {
        this.displayRoute(route[0], route[1]);
      }
    });

    this.getCurrentVehiclesLocation();

    this.i1 = setInterval(() => {
      this.getCurrentVehiclesLocation();
    }, this.dataRefreshIntervalInSeconds * 1000);

    this.i2 = setInterval(() => {
      this.updateVehiclesLocation();
      this.period =
        (this.period + this.mapRefreshIntervalInSeconds) %
        this.dataRefreshIntervalInSeconds;
    }, this.mapRefreshIntervalInSeconds * 1000);
  }

  ngOnDestroy(): void {
    this.mapService.resetRoutes();
    this.mapSubscription.unsubscribe();
    this.vehiclesSubscription.unsubscribe();
    if (this.i1) {
      clearInterval(this.i1);
    }
    if (this.i2) {
      clearInterval(this.i2);
    }
  }

  setUpMap() {
    //@ts-ignore
    window.initMap = function () {};
  }

  iconBase = '../../../../assets';

  iconFree = {
    url: this.iconBase + '/car-icon.png',
    scaledSize: new google.maps.Size(40, 40),
  };
  iconInRide = {
    url: this.iconBase + '/car-icon-blue.png',
    scaledSize: new google.maps.Size(40, 40),
  };
  iconPanic = {
    url: this.iconBase + '/car-icon-red.png',
    scaledSize: new google.maps.Size(40, 40),
  };

  markerOptionsBasic: google.maps.MarkerOptions = {
    draggable: false,
    optimized: true,
  };
  markerOptionsFree: google.maps.MarkerOptions = {
    draggable: false,
    icon: this.iconFree,
    optimized: true,
  };
  markerOptionsInRide: google.maps.MarkerOptions = {
    draggable: false,
    icon: this.iconInRide,
    optimized: true,
  };
  markerOptionsPanic: google.maps.MarkerOptions = {
    draggable: false,
    icon: this.iconPanic,
    optimized: true,
  };

  allActiveVehicles: VehicleDTO[] = [];
  allActiveVehiclesOld: VehicleDTO[] = [];

  markerPositionsBasic: google.maps.LatLngLiteral[] = [];
  markerPositionsFree: google.maps.LatLngLiteral[] = [];
  markerPositionsInRide: google.maps.LatLngLiteral[] = [];
  markerPositionsPanic: google.maps.LatLngLiteral[] = [];

  moveMap(event: google.maps.MapMouseEvent) {
    if (event.latLng != null) this.center = event.latLng.toJSON();
  }
  move(event: google.maps.MapMouseEvent) {
    if (event.latLng != null) this.display = event.latLng.toJSON();
  }
  addMarker(event: google.maps.MapMouseEvent) {
    if (event.latLng != null) {
      if (this.markerPositionsBasic.length >= 2) {
        this.markerPositionsBasic = [];
      }
      this.markerPositionsBasic.push(event.latLng.toJSON());
      this.mapService
        .getAddressFromLatLng(
          event.latLng.toJSON()['lat'],
          event.latLng.toJSON()['lng']
        )
        .subscribe((address) => {
          this.mapService.addPinAddressToForm(
            address.results[0].formatted_address
          );
        });
    }
  }
  initMap() {}

  getUserLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position: GeolocationPosition) => {
          const userPosition = {
            lat: position.coords.latitude,
            lng: position.coords.longitude,
          };
          this.center = userPosition;
        },
        () => {
          this.center = { lat: 45.2396, lng: 19.8227 };
        }
      );
    } else {
      // Browser doesn't support Geolocation
      this.center = { lat: 45.2396, lng: 19.8227 };
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

  getCurrentVehiclesLocation() {
    this.vehiclesSubscription = this.mapService
      .getAllActiveVehicles()
      .subscribe((vehicles) => {
        this.allActiveVehiclesOld = this.allActiveVehicles;
        this.allActiveVehicles = vehicles;
      });
  }

  findVehiclePair(vehicle: VehicleDTO): VehicleDTO | null {
    let result = this.allActiveVehiclesOld.filter((v) => {
      return v.id == vehicle.id;
    });
    if (result.length == 1) {
      return result[0];
    }
    return null;
  }

  updateVehiclesLocation() {
    this.markerPositionsFree = [];
    this.markerPositionsInRide = [];
    this.markerPositionsPanic = [];
    for (let vehicle of this.allActiveVehicles) {
      const vehicleOld = this.findVehiclePair(vehicle);
      let vehicleLatLng: google.maps.LatLngLiteral;
      if (vehicleOld == null) {
        vehicleLatLng = {
          lat: vehicle.currentLocation.latitude,
          lng: vehicle.currentLocation.longitude,
        };
      } else {
        vehicleLatLng = {
          lat:
            vehicleOld.currentLocation.latitude +
            ((vehicle.currentLocation.latitude -
              vehicleOld.currentLocation.latitude) /
              this.dataRefreshIntervalInSeconds) *
              this.period,
          lng:
            vehicleOld.currentLocation.longitude +
            ((vehicle.currentLocation.longitude -
              vehicleOld.currentLocation.longitude) /
              this.dataRefreshIntervalInSeconds) *
              this.period,
        };
      }
      if (vehicle.status == 'FREE') {
        this.markerPositionsFree.push(vehicleLatLng);
      } else if (vehicle.status == 'INRIDE') {
        this.markerPositionsInRide.push(vehicleLatLng);
      } else if (vehicle.status == 'PANIC') {
        this.markerPositionsPanic.push(vehicleLatLng);
      } else {
        console.log(`Invalide ride state ${vehicle.status}`);
      }
    }
  }
}
