import { Component, OnDestroy, OnInit } from '@angular/core';
import * as SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

import { RideService } from '../../services/ride.service';
import { RideDTO } from '../../models/ride-dto';
import { DriverService } from '../../../driver/services/driver.service';
import { PanicService } from '../../services/panic.service';
import { ReasonDTO } from '../../models/reason-dto';
import { NoteDTO } from '../../models/note-dto';
import { NoteService } from '../../services/note.service';
import { AdminNotificationDTO } from '../../../administrator/models/admin-notification-dto';
import { MapService } from 'src/app/universal-components/services/map.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/auth/services/auth.service';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

@Component({
  selector: 'app-passenger-current-ride-info',
  templateUrl: './passenger-current-ride-info.component.html',
  styleUrls: ['./passenger-current-ride-info.component.css'],
})
export class PassengerCurrentRideInfoComponent implements OnInit, OnDestroy {
  price: number = 0;
  arrival: string = '00:00';
  carModel: string = '';
  driverName: string = '';
  driverSurname: string = '';
  elapsedTime: number = 0;
  minutes: number = 0;
  seconds: number = 0;
  ride!: RideDTO;
  stompClient: any;
  url: string = 'http://localhost:8080';
  isLoaded = false;
  rideInProgress = false;
  timer: any;

  constructor(
    private rideService: RideService,
    private driverService: DriverService,
    private panicService: PanicService,
    private noteService: NoteService,
    private mapService: MapService,
    private router: Router,
    private authService: AuthService,
    private popUpService: PopUpService
  ) {}

  currentRideSubscription: Subscription = new Subscription();

  ngOnInit(): void {
    this.currentRideSubscription =
      this.rideService.selectedCurrentRide$.subscribe((response) => {
        this.ride = response;
        this.rideInProgress = this.ride.status === 'ACTIVE';
        if (this.rideInProgress) {
          this.countTime();
        }
        this.price = response.totalCost;
        this.arrival = response.endTime.split('T')[1].split('.')[0];
        this.driverService
          .getDriver(response.driver!.email)
          .subscribe((driver) => {
            this.driverName = driver.name;
            this.driverSurname = driver.surname;
            this.driverService
              .getDriversVehicle(driver.id)
              .subscribe((vehicle) => {
                this.carModel = vehicle.model;
              });
          });
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
      that.openSocketCancelation();
      that.openSocketStart();
      that.openSocketEnd();
    });
  }

  openSocketCancelation() {
    if (this.isLoaded) {
      this.stompClient.subscribe(
        '/socket-out/cancel/' + this.authService.getId(),
        () => {
          this.popUpService.showPopUp(
            'Driver canceled ride :(\nContact support for more details'
          );
          this.router.navigateByUrl('home');
        }
      );
    }
  }

  openSocketStart() {
    if (this.isLoaded) {
      this.stompClient.subscribe(
        '/socket-out/ride-started/' + this.authService.getId(),
        () => {
          this.rideInProgress = true;
          this.countTime();
        }
      );
    }
  }

  openSocketEnd() {
    if (this.isLoaded) {
      this.stompClient.subscribe(
        '/socket-out/ride-ended/' + this.authService.getId(),
        () => {
          this.router.navigateByUrl('home');
        }
      );
    }
  }

  ngOnDestroy(): void {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
    this.currentRideSubscription.unsubscribe();
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  countTime() {
    this.timer = setInterval(() => {
      this.elapsedTime++;
      this.minutes = Math.floor(this.elapsedTime / 60);
      this.seconds = this.elapsedTime % 60;
    }, 1000);
  }

  report() {
    // @ts-ignore
    const noteText: string = document.getElementById('reasonForm').value;
    if (noteText != '')
      this.noteService
        .makeNote(this.ride.driver!.id, <NoteDTO>{ message: noteText })
        .subscribe((response) => {
          this.popUpService.showPopUp('Driver reported successfully');
        });
    else {
      this.popUpService.showPopUp("Reason for report can't be empty");
    }
  }

  panic() {
    // @ts-ignore
    const reasonText: string = document.getElementById('reasonForm').value;
    if (reasonText != '')
      this.panicService
        .panic(this.ride.id, <ReasonDTO>{ reason: reasonText })
        .subscribe((response) => {
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
          this.router.navigateByUrl('passenger-main');
        });
    else {
      this.popUpService.showPopUp('Reason for panic not specified');
    }
  }

  withdraw() {
    this.rideService.passengerWithdrawFromRide(this.ride.id).subscribe({
      next: (response) => {
        this.router.navigateByUrl('home');
      },
      error: (error: HttpErrorResponse) => {
        this.popUpService.showPopUp(error.error.message);
      },
    });
  }
}
