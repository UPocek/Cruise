import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ResetPasswordService {
  urlExtension = '/user/';

  constructor(private http: HttpClient) {}

  forgotPassword(id: number): Observable<void> {
    return this.http.get<void>(
      `${environment.urlBase}${this.urlExtension}${id}/resetPassword`,
    );
  }
}
