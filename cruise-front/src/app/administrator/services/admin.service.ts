import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';

import { AdminDTO } from '../../user/models/admin-dto';
import { environment } from 'src/environments/environment';
import {AdminNotificationDTO} from "../models/admin-notification-dto";

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  url = `${environment.urlBase}/admin/`;

  private notificationForm$ = new BehaviorSubject<string>("");
  selectedNotificationForm$ = this.notificationForm$.asObservable();


  constructor(private http: HttpClient) {}

  getAdmin(email: string): Observable<AdminDTO> {
    return this.http.get<AdminDTO>(this.url + 'username=' + email);
  }

  updateAdmin(id: string, user: AdminDTO): Observable<AdminDTO> {
    return this.http.put<AdminDTO>(this.url + id, user);
  }

  setNotificationForm(product: string) {
    this.notificationForm$.next(product);
  }

}
