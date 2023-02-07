import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { RideDTO } from '../models/ride-dto';

@Injectable({
  providedIn: 'root',
})
export class HistoryService {
  selectedRide$ = new BehaviorSubject<RideDTO | null>(null);

  selectNewRide(ride: RideDTO) {
    this.selectedRide$.next(ride);
  }

  constructor() {}
}
