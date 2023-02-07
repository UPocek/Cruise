import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {BehaviorSubject, observable, Observable} from 'rxjs';
import { environment } from 'src/environments/environment';
import { EmailDTO } from '../models/email-dto';
import { RideDTO } from '../models/ride-dto';
import { RideRequestDTO } from '../models/ride-request-dto';
import {AuthService} from "../../auth/services/auth.service";
import {ReasonDTO} from "../models/reason-dto";
import {PanicDTO} from "../models/panic-dto";

@Injectable({
  providedIn: 'root',
})
export class PanicService {
  url = `${environment.urlBase}`;


  constructor(private httpClient: HttpClient, private authService: AuthService) {}

  panic(ride: number, reason: ReasonDTO): Observable<PanicDTO> {
    return this.httpClient.put<PanicDTO>(`${this.url}/ride/${ride}/panic`, reason);
  }

}
