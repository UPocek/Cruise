import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';

import { AdminDTO } from '../../user/models/admin-dto';
import { environment } from 'src/environments/environment';
import {PanicsDTO} from "../models/panics-dto";
import {PanicDTO} from "../../user/models/panic-dto";

@Injectable({
  providedIn: 'root',
})
export class PanicService {
  url = `${environment.urlBase}/panic`;

  private panic$ = new BehaviorSubject<PanicDTO>(<PanicDTO>{});
  selectedPanic$ = this.panic$.asObservable();

  constructor(private http: HttpClient) {}

  getPanics(): Observable<PanicsDTO> {
    return this.http.get<PanicsDTO>(this.url)
  }

  setPanic(panic: PanicDTO) {
    this.panic$.next(panic);
  }
}
