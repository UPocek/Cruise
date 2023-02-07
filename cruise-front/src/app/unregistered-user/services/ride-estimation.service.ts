import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { OfferDTO } from '../models/offer-dto';
import { RideInfoDTO } from '../models/ride-basic-info-dto';
import { DistanceDTO } from '../models/distance-dto';

@Injectable({
  providedIn: 'root',
})
export class RideEstimationService {
  urlExtension = '/unregisteredUser';

  constructor(private http: HttpClient) {}

  requestRideEstimation(rideInfo: RideInfoDTO): Observable<OfferDTO> {
    return this.http.post<OfferDTO>(
      `${environment.urlBase}${this.urlExtension}`,
      rideInfo
    );
  }

  requestRideDistanceEstimation(
    rideInfo: RideInfoDTO
  ): Observable<DistanceDTO> {
    return this.http.post<DistanceDTO>(
      `${environment.urlBase}${this.urlExtension}` + '/distance',
      rideInfo
    );
  }
}
