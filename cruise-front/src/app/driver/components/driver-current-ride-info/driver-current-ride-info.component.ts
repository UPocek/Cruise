import { Component, OnDestroy, OnInit } from '@angular/core';
import { Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

import { ReasonDTO } from '../../../user/models/reason-dto';
import { RideService } from '../../../user/services/ride.service';
import { PanicService } from '../../../user/services/panic.service';
import { RideDTO } from '../../../user/models/ride-dto';
import { UserForRideDTO } from '../../../user/models/user-for-ride-dto';
import { AdminNotificationDTO } from '../../../administrator/models/admin-notification-dto';
import { MapService } from '../../../universal-components/services/map.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from 'src/app/auth/services/auth.service';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-driver-current-ride-info',
  templateUrl: './driver-current-ride-info.component.html',
  styleUrls: ['./driver-current-ride-info.component.css'],
})
export class DriverCurrentRideInfoComponent implements OnInit, OnDestroy {
  price: number = 0;
  arrival: string = '00:00';
  passengers: UserForRideDTO[] = [];
  elapsedTime: number = 0;
  minutes: number = 0;
  seconds: number = 0;
  ride!: RideDTO;
  stompClient: any;
  url: string = environment.serverUrl;
  isLoaded = false;
  rideInProgress = false;
  timer?: any;

  constructor(
    private rideService: RideService,
    private panicService: PanicService,
    private mapService: MapService,
    private authService: AuthService,
    private popUpService: PopUpService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.rideService.selectedCurrentRide$.subscribe((response) => {
      this.ride = response;
      this.passengers = response.passengers;
      this.rideInProgress = this.ride.status === 'ACTIVE';
      if (this.rideInProgress) {
        this.countTime();
      }
      this.price = response.totalCost;
      this.arrival = response.endTime.split('T')[1].split('.')[0];
      this.mapService.requestRoute(
        {
          lat: this.ride.locations[0].departure.latitude,
          lng: this.ride.locations[0].departure.longitude,
        },
        {
          lat: this.ride.locations[0].destination.latitude,
          lng: this.ride.locations[0].destination.longitude,
        }
      );
    });

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
      this.stompClient.subscribe(
        '/socket-out/withdraw/' + this.authService.getId(),
        () => {
          this.popUpService.showPopUp('Passenger withdrawn from the ride :(');
          window.location.reload();
        }
      );
    }
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  }

  countTime() {
    this.timer = setInterval(() => {
      this.elapsedTime++;
      this.minutes = Math.floor(this.elapsedTime / 60);
      this.seconds = this.elapsedTime % 60;
    }, 1000);
  }

  panic() {
    // @ts-ignore
    const reasonText: string = document.getElementById('reasonForm').value;
    if (reasonText != null && reasonText.length >= 5)
      this.panicService
        .panic(this.ride.id, <ReasonDTO>{ reason: reasonText })
        .subscribe({
          next: (response) => {
            this.popUpService.showPopUp('Successful panic');
            this.stompClient.send(
              '/socket-in/panic-notification',
              {},
              JSON.stringify(<AdminNotificationDTO>(<unknown>{
                isPanic: true,
                panic: response,
                time: response.time.replace(' ', 'T'),
              }))
            );
            this.router.navigateByUrl('/driver-main');
          },
          error: (error: HttpErrorResponse) => {
            this.popUpService.showPopUp(error.error.message);
          },
        });
    else {
      this.popUpService.showPopUp(
        'Reason for panic must be at least 5 characters long'
      );
    }
  }

  startRide() {
    this.rideService.startRide(this.ride.id).subscribe(() => {
      this.rideInProgress = true;
    });
    this.countTime();
  }

  endRide() {
    this.rideService.endRide(this.ride.id).subscribe(() => {
      this.rideInProgress = false;
      window.location.reload();
    });
  }

  cancelRide() {
    // @ts-ignore
    const reasonText: string = document.getElementById('reasonForm').value;
    if (reasonText != null && reasonText.length >= 5) {
      this.rideService
        .declineRide(this.ride.id, { reason: reasonText })
        .subscribe({
          next: (ride) => {
            window.location.reload();
          },
          error: (error: HttpErrorResponse) => {
            this.popUpService.showPopUp(error.error.message);
          },
        });
    } else {
      this.popUpService.showPopUp(
        'Reason for cancelation must be at least 5 characters long'
      );
    }
  }
}
