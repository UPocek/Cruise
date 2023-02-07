import { Component, OnDestroy, OnInit } from '@angular/core';
import * as SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { Subscription } from 'rxjs';
import { DriverService } from '../../services/driver.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-driver-main-page',
  templateUrl: './driver-main-page.component.html',
  styleUrls: ['./driver-main-page.component.css'],
})
export class DriverMainPageComponent implements OnInit, OnDestroy {
  url = 'http://localhost:8080';
  isLoaded = false;
  stompClient: any;
  rideToDriveSubscription: Subscription = new Subscription();
  userId: number = -1;

  constructor(
    private driverService: DriverService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userId = this.authService.getId();
    this.rideToDriveSubscription = this.driverService
      .checkForRidesAssignedToDriver()
      .subscribe({
        next: (ride) => {
          if (
            ride != null &&
            !this.driverService.rideIdsOfAnsweredRideRequests.includes(ride.id)
          ) {
            this.driverService.setDriverRide(ride);
            this.router.navigateByUrl('/driver-ride-request');
          } else {
            this.initializeWebSocketConnection();
          }
        },
        error: () => {
          this.initializeWebSocketConnection();
        },
      });
  }

  ngOnDestroy(): void {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
    this.rideToDriveSubscription.unsubscribe();
  }

  initializeWebSocketConnection() {
    let that = this;

    this.stompClient = Stomp.over(function () {
      return new SockJS(`${that.url}/socket`);
    });

    this.stompClient.connect({}, function () {
      that.isLoaded = true;
      that.openSocket();
    });
  }

  openSocket() {
    if (this.isLoaded) {
      this.stompClient.subscribe('/socket-out/' + this.userId, (ride: any) => {
        this.driverService.setDriverRide(JSON.parse(ride.body));
        this.router.navigateByUrl('/driver-ride-request');
      });
    }
  }
}
