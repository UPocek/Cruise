import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, observable, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { EmailDTO } from '../models/email-dto';
import { RideDTO } from '../models/ride-dto';
import { RideRequestDTO } from '../models/ride-request-dto';

@Injectable({
  providedIn: 'root',
})
export class RideRequestService {
  private ride$ = new BehaviorSubject(<any>{});
  rideRequest$ = this.ride$.asObservable();

  setRide(ride: RideDTO) {
    this.ride$.next(ride);
  }

  private emails$ = new BehaviorSubject(<any>{});
  emailsSent$ = this.emails$.asObservable();

  setEmails(emails: String[]) {
    this.emails$.next(emails);
  }

  url = `${environment.urlBase}`;

  constructor(private httpClient: HttpClient) {}

  sendRideRequest(ride: RideRequestDTO): Observable<RideDTO> {
    return this.httpClient.post<RideDTO>(`${this.url}/ride`, ride);
  }

  sendRideInvitations(emails: EmailDTO) {
    return this.httpClient.post(`${this.url}/ride/invitation`, emails);
  }
}
