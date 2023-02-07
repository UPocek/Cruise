import { Component, Input, OnInit } from '@angular/core';
import { UserForRideDTO } from '../../../user/models/user-for-ride-dto';
import { PassengerService } from '../../../user/services/passenger.service';

@Component({
  selector: 'app-passenger-for-ride',
  templateUrl: './passenger-for-ride.component.html',
  styleUrls: ['./passenger-for-ride.component.css'],
})
export class PassengerForRideComponent implements OnInit {
  passengerName: string = 'Tamara';
  passengerSurname: string = 'Ilic';
  @Input() passenger!: UserForRideDTO;
  @Input() rideId!: number;

  constructor(private passengerService: PassengerService) {}

  ngOnInit(): void {
    this.passengerService
      .getPassenger(this.passenger.email)
      .subscribe((response) => {
        this.passengerName = response.name;
        this.passengerSurname = response.surname;
      });
  }
}
