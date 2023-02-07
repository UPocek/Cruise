import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/services/auth.service';
import { AnswerEmailDTO } from '../../models/answer-email-dto';
import { PassengerService } from '../../services/passenger.service';

@Component({
  selector: 'app-passenger-main-page',
  templateUrl: './passenger-main-page.component.html',
  styleUrls: ['./passenger-main-page.component.css'],
})
export class PassengerMainPageComponent implements OnInit {
  passengerId!: number;
  rideInvites: AnswerEmailDTO[] = [];
  canRequestRide: boolean = true;
  constructor(
    private passengerService: PassengerService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.passengerId = this.authService.getId();
    this.passengerService
      .getPassengerInvites(this.passengerId)
      .subscribe((response) => {
        this.rideInvites = response;
        if (this.rideInvites.length > 0) {
          this.canRequestRide = false;
        }
      });
  }

  removeInvitation(invite: AnswerEmailDTO) {
    const index = this.rideInvites.indexOf(invite);

    if (index > -1) {
      this.rideInvites.splice(index, 1);
    }

    if (this.rideInvites.length == 0) {
      this.canRequestRide = true;
    }
  }
}
