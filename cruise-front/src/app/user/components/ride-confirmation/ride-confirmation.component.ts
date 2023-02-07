import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { AuthService } from 'src/app/auth/services/auth.service';
import { RideDTO } from '../../models/ride-dto';
import { RideRequestService } from '../../services/ride-request.service';

@Component({
  selector: 'app-ride-confirmation',
  templateUrl: './ride-confirmation.component.html',
  styleUrls: ['./ride-confirmation.component.css'],
})
export class RideConfirmationComponent implements OnInit, OnDestroy {
  passengerId!: number;
  rideRequestStatus: number = 0;
  url: string = 'http://localhost:8080';
  isLoaded = false;
  private stompClient: any;
  ride: RideDTO = <RideDTO>{};
  driverEmail = '';
  vehicleType = '';

  constructor(
    private rideRequestService: RideRequestService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.rideRequestService.rideRequest$.subscribe((ride) => {
      this.ride = ride;
    });

    this.passengerId = this.authService.getId();

    let that = this;
    this.stompClient = Stomp.over(function () {
      return new SockJS(`${that.url}/socket`);
    });

    this.stompClient.connect({}, function () {
      that.isLoaded = true;
      that.openSocket();
    });
  }

  ngOnDestroy(): void {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  }

  openSocket() {
    if (this.isLoaded) {
      this.stompClient.subscribe(
        `/socket-out/${this.passengerId}`,
        (rideResponse: any) => {
          this.handleResult(JSON.parse(rideResponse.body));
        }
      );

      this.stompClient.send(
        '/socket-in/ride-request',
        {},
        JSON.stringify(this.ride)
      );
    }
  }

  handleResult(rideResponse: RideDTO) {
    if (rideResponse.status == 'ACCEPTED') {
      this.rideRequestStatus = 1;
      this.driverEmail = rideResponse.driver!.email;
      this.vehicleType = rideResponse.vehicleType;
    } else if (rideResponse.status == 'FORBIDDEN') {
      this.rideRequestStatus = -2;
    } else {
      this.rideRequestStatus = -1;
    }
  }

  navigateBack() {
    this.router.navigateByUrl('/current-ride');
  }
}
