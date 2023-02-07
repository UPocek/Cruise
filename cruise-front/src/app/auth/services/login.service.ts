import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

import { RegisteredUserDTO } from '../../user/models/registered-user-dto';
import { LoginDTO } from '../models/login-dto';
import { environment } from 'src/environments/environment';
import { CredentialsDTO } from '../models/credentials-dto';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  urlExtension = '/user';

  private user$ = new BehaviorSubject<RegisteredUserDTO>(<RegisteredUserDTO>{});
  registeredUser$ = this.user$.asObservable();

  setRegisteredUser(registeredUser: RegisteredUserDTO) {
    this.user$.next(registeredUser);
  }

  constructor(private http: HttpClient) {}

  loginUser(loginDTO: CredentialsDTO): Observable<LoginDTO> {
    return this.http.post<LoginDTO>(
      `${environment.urlBase}${this.urlExtension}/login`,
      loginDTO
    );
  }
  getUser(email: string): Observable<boolean> {
    return this.http.get<boolean>(
      `${environment.urlBase}${this.urlExtension}/isBlocked/${email}`
    );
  }
}
