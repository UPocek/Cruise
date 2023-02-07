import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { UserDTO } from '../models/user-dto';
import { RegisteredUserDTO } from '../models/registered-user-dto';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class RegistrationService {
  urlExtension = '/passenger';

  constructor(private http: HttpClient) {}

  registerUser(userDTO: UserDTO): Observable<RegisteredUserDTO> {
    return this.http.post<RegisteredUserDTO>(
      `${environment.urlBase}${this.urlExtension}`,
      userDTO
    );
  }
}
