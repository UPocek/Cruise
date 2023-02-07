import { Component, OnInit } from '@angular/core';

import { RideService } from '../../services/ride.service';
import { AuthService } from '../../../auth/services/auth.service';
import { error } from '@angular/compiler-cli/src/transformers/util';

@Component({
  selector: 'app-current-ride',
  templateUrl: './current-ride.component.html',
  styleUrls: ['./current-ride.component.css'],
})
export class CurrentRideComponent implements OnInit {
  isDriver: boolean = false;
  isPassanger: boolean = false;

  constructor(
    private rideService: RideService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadComponentsIfRideExists();
  }

  loadComponentsIfRideExists() {
    if (this.authService.getRole() === 'ROLE_PASSENGER') {
      this.loadPassengerComponents();
    } else if (this.authService.getRole() === 'ROLE_DRIVER') {
      this.loadDriverComponents();
    }
  }

  loadDriverComponents() {
    this.rideService.getActiveRideForDriver().subscribe({
      next: (response) => {
        if (response != null) {
          this.isDriver = true;
          this.rideService.setCurrentRide(response);
        }
      },

      error: (error) => {
        this.rideService.getAcceptedRideForDriver().subscribe({
          next: (response) => {
            if (response != null) {
              this.isDriver = true;
              this.rideService.setCurrentRide(response);
            }
          },
          error: () => [],
        });
      },
    });
  }

  loadPassengerComponents() {
    this.rideService.getActiveRideForPassenger().subscribe({
      next: (response) => {
        if (response != null) {
          this.isPassanger = true;
          this.rideService.setCurrentRide(response);
        }
      },

      error: (error) => {
        this.rideService.getAcceptedRideForPassenger().subscribe({
          next: (response) => {
            if (response != null) {
              this.isPassanger = true;
              this.rideService.setCurrentRide(response);
            }
          },
          error: () => {},
        });
      },
    });
  }
}
