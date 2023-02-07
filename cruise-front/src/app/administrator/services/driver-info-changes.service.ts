import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

import { DriverChangesDTO } from '../models/driver-changes-dto';
import { UserDTO } from '../../user/models/user-dto';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DriverInfoChangesService {
  headers_object = new HttpHeaders({
    'Content-Type': 'application/json',
    Authorization: 'Bearer ' + window.sessionStorage.getItem('jwt'),
  });

  http_options = {
    headers: this.headers_object,
  };

  private product$ = new BehaviorSubject<any>({});
  selectedProduct$ = this.product$.asObservable();

  url = `${environment.urlBase}/driver`;

  constructor(private http: HttpClient) {}

  getAllRequests(): Observable<DriverChangesDTO[]> {
    return this.http.get<DriverChangesDTO[]>(
      this.url + '/changeRequests',
      this.http_options
    );
  }

  setProduct(product: any) {
    this.product$.next(product);
  }

  approve(
    id: number | undefined,
    idrequest: number | undefined,
    dto: UserDTO
  ): Observable<UserDTO> {
    return this.http.put<UserDTO>(
      this.url + '/' + id + '/' + idrequest,
      dto,
      this.http_options
    );
  }

  reject(id: number | undefined): Observable<void> {
    return this.http.delete<void>(
      this.url + '/rejectChanges/' + id,
      this.http_options
    );
  }
}
