import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { UserDTO } from '../models/user-dto';
import { RegisteredUserDTO } from '../models/registered-user-dto';
import { environment } from 'src/environments/environment';
import { AnswerEmailDTO } from '../models/answer-email-dto';

@Injectable({
  providedIn: 'root',
})
export class PassengerService {
  url = `${environment.urlBase}/passenger/`;
  urlInvitations = `${environment.urlBase}/ride/invitations/`;

  constructor(private http: HttpClient) {}

  getPassenger(email: string): Observable<RegisteredUserDTO> {
    return this.http.get<RegisteredUserDTO>(this.url + 'email=' + email);
  }

  updatePassenger(id: string, user: UserDTO): Observable<UserDTO> {
    return this.http.put<UserDTO>(this.url + id, user);
  }

  getPassengerInvites(id: number): Observable<AnswerEmailDTO[]> {
    return this.http.get<AnswerEmailDTO[]>(`${this.urlInvitations}${id}`);
  }
}
