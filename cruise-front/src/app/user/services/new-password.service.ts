import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { CredentialsDTO } from '../../auth/models/credentials-dto';
import {ResetPasswordDTO} from "../models/reset-password-dto";

@Injectable({
  providedIn: 'root',
})
export class NewPasswordService {
  constructor(private http: HttpClient) {}

  urlExtension = '/user';

  changePassword(id: number, request: ResetPasswordDTO): Observable<void> {
    return this.http.put<void>(
      `${environment.urlBase}${this.urlExtension}/${id}/resetPassword`,
      request
    );
  }
}
