import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { NoteDTO } from '../models/note-dto';

@Injectable({
  providedIn: 'root',
})
export class ConfirmAccountService {
  constructor(private http: HttpClient) {}

  urlExtension = '/passenger/activate/';

  verifyUser(verificationId: number): Observable<NoteDTO> {
    return this.http.get<NoteDTO>(
      `${environment.urlBase}${this.urlExtension}${verificationId}`
    );
  }
}
