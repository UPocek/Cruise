import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, EMPTY, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { RideDTO } from '../models/ride-dto';
import { AuthService } from '../../auth/services/auth.service';
import { PassengerRides } from '../models/passenger-rides-dto';
import { ReportsDTO } from '../models/reports-dto';
import { ReasonDTO } from '../models/reason-dto';

@Injectable({
  providedIn: 'root',
})
export class RideService {
  url = `${environment.urlBase}`;

  private currentRide$ = new BehaviorSubject<RideDTO>(<RideDTO>{});
  selectedCurrentRide$ = this.currentRide$.asObservable();

  setCurrentRide(product: any) {
    this.currentRide$.next(product);
  }

  constructor(private http: HttpClient, private authService: AuthService) {}

  getActiveRideForPassenger(): Observable<RideDTO> {
    return this.http.get<RideDTO>(
      `${this.url}/ride/passenger/${this.authService.getId()}/active`
    );
  }
  getActiveRideForDriver(): Observable<RideDTO> {
    return this.http.get<RideDTO>(
      `${this.url}/ride/driver/${this.authService.getId()}/active`
    );
  }
  getAcceptedRideForPassenger(): Observable<RideDTO> {
    return this.http.get<RideDTO>(
      `${this.url}/ride/passenger/${this.authService.getId()}/accepted`
    );
  }
  getAcceptedRideForDriver(): Observable<RideDTO> {
    return this.http.get<RideDTO>(
      `${this.url}/ride/driver/${this.authService.getId()}/accepted`
    );
  }

  startRide(rideId: number): Observable<RideDTO> {
    return this.http.put<RideDTO>(`${this.url}/ride/${rideId}/start`, null);
  }

  endRide(rideId: number): Observable<RideDTO> {
    return this.http.put<RideDTO>(`${this.url}/ride/${rideId}/end`, null);
  }

  declineRide(rideId: number, reason: ReasonDTO): Observable<RideDTO> {
    return this.http.put<RideDTO>(`${this.url}/ride/${rideId}/cancel`, reason);
  }

  getAllUserRides(
    id: number,
    from: string = '',
    to: string = '',
    sort: string = ''
  ): Observable<PassengerRides> {
    return this.http.get<PassengerRides>(
      `${this.url}/passenger/${id}/ride?from=${from}&to=${to}&sort=${sort}`
    );
  }

  getPassengerReports(
    id: number,
    fromDate: string,
    tillDate: string
  ): Observable<ReportsDTO> {
    return this.http.get<ReportsDTO>(
      `${this.url}/ride/reports/${id}/passenger?from=${fromDate}&to=${tillDate}`
    );
  }

  getDriverReports(
    id: number,
    fromDate: string,
    tillDate: string
  ): Observable<ReportsDTO> {
    return this.http.get<ReportsDTO>(
      `${this.url}/ride/reports/${id}/driver?from=${fromDate}&to=${tillDate}`
    );
  }

  getAllReports(fromDate: string, tillDate: string): Observable<ReportsDTO> {
    return this.http.get<ReportsDTO>(
      `${this.url}/ride/reports?from=${fromDate}&to=${tillDate}`
    );
  }

  getUserReports(
    email: string,
    fromDate: string,
    tillDate: string
  ): Observable<ReportsDTO> {
    return this.http.get<ReportsDTO>(
      `${this.url}/ride/reports/${email}?from=${fromDate}&to=${tillDate}`
    );
  }

  passengerWithdrawFromRide(rideId: number) {
    return this.http.put<RideDTO>(`${this.url}/ride/${rideId}/withdraw`, null);
  }

  getRideById(rideId: number): Observable<RideDTO> {
    return this.http.get<RideDTO>(`${this.url}/ride/${rideId}`);
  }
}
