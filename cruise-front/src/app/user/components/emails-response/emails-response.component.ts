import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, RouteReuseStrategy } from '@angular/router';
import { Stomp } from '@stomp/stompjs';
import { Subscription } from 'rxjs';
import * as SockJS from 'sockjs-client';
import { AuthService } from 'src/app/auth/services/auth.service';
import { AnswerEmailDTO } from '../../models/answer-email-dto';
import { RideDTO } from '../../models/ride-dto';
import { PassengerService } from '../../services/passenger.service';
import { RideRequestService } from '../../services/ride-request.service';

@Component({
  selector: 'app-emails-response',
  templateUrl: './emails-response.component.html',
  styleUrls: ['./emails-response.component.css'],
})
export class EmailsResponseComponent implements OnInit, OnDestroy {
  emails: string[] = [];
  private subscription = new Subscription();
  url: string = 'http://localhost:8080';
  isLoaded = false;
  private stompClient: any;
  passengerId!: number;
  ride: RideDTO = <RideDTO>{};
  acceptedRidePassengers: string[] = [];
  rejectedRidePassengers: string[] = [];

  constructor(
    private rideRequestService: RideRequestService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.passengerId = this.authService.getId();
    this.subscription = this.rideRequestService.emailsSent$.subscribe(
      (response) => {
        this.emails = response;
      }
    );

    this.rideRequestService.rideRequest$.subscribe((ride) => {
      this.ride = ride;
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

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  }

  handleResult(answer: AnswerEmailDTO) {
    const index = this.emails.indexOf(answer.receiverPassengerEmail);

    if (index > -1) {
      this.emails.splice(index, 1);
    }

    if (answer.answer == 'YES') {
      this.acceptedRidePassengers.push(answer.receiverPassengerEmail);
    } else {
      this.rejectedRidePassengers.push(answer.receiverPassengerEmail);
    }
  }

  openSocket() {
    if (this.isLoaded) {
      this.stompClient.subscribe(
        `/socket-out-invite/${this.passengerId}`,
        (answer: any) => {
          this.handleResult(JSON.parse(answer.body));
        }
      );
    }
  }

  createRequest() {
    this.router.navigateByUrl('/ride-confirmation');
  }
}
