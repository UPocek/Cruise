import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

import { UserDTO } from '../../user/models/user-dto';
import { environment } from 'src/environments/environment';
import { PassengerListDTO } from '../models/passenger-list-dto';
import { DriverListDTO } from '../models/driver-list-dto';
import {UserNotesDTO} from "../models/user-notes-dto";
import {RegisteredUserDTO} from "../../user/models/registered-user-dto";

@Injectable({
  providedIn: 'root',
})
export class UserInfoService {
  private product$ = new BehaviorSubject<any>({});
  selectedProduct$ = this.product$.asObservable();

  private id$ = new BehaviorSubject<number>(0);
  selectedId$ = this.id$.asObservable();

  passangerUrl = `${environment.urlBase}/passenger`;
  driverUrl = `${environment.urlBase}/driver`;
  userUrl = `${environment.urlBase}/user`;

  constructor(private http: HttpClient) {}

  getAllPassengers(page: number, size: number): Observable<PassengerListDTO> {
    return this.http.get<PassengerListDTO>(
      this.passangerUrl + '?page=' + page + '&size=' + size
    );
  }

  getAllDrivers(page: number, size: number): Observable<DriverListDTO> {
    return this.http.get<DriverListDTO>(
      this.driverUrl + '?page=' + page + '&size=' + size
    );
  }

  setProduct(product: any) {
    this.product$.next(product);
  }

  setId(id: number) {
    this.id$.next(id);
  }

  blockPassenger(email: string): Observable<UserDTO> {
    return this.http.put<UserDTO>(this.passangerUrl + '/block/' + email, null);
  }

  unblockPassenger(email: string): Observable<UserDTO> {
    return this.http.put<UserDTO>(
      this.passangerUrl + '/unblock/' + email,
      null
    );
  }

  blockDriver(email: string): Observable<UserDTO> {
    return this.http.put<UserDTO>(this.driverUrl + '/block/' + email, null);
  }

  unblockDriver(email: string): Observable<UserDTO> {
    return this.http.put<UserDTO>(this.driverUrl + '/unblock/' + email, null);
  }

  getUserNotes(id: number): Observable<UserNotesDTO> {
    return this.http.get<UserNotesDTO>(`${this.userUrl}/${id}/note`);
  }

  getUserByEmail(email: string): Observable<RegisteredUserDTO> {
    return this.http.get<RegisteredUserDTO>(`${this.userUrl}/email/${email}`);
  }
}
