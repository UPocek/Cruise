import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {BehaviorSubject, observable, Observable} from 'rxjs';
import { environment } from 'src/environments/environment';
import { EmailDTO } from '../models/email-dto';
import { RideDTO } from '../models/ride-dto';
import { RideRequestDTO } from '../models/ride-request-dto';
import {AuthService} from "../../auth/services/auth.service";
import {NoteDTO} from "../models/note-dto";
import {NoteWithDateDTO} from "./note-with-date-dto";

@Injectable({
  providedIn: 'root',
})
export class NoteService {
  url = `${environment.urlBase}`;


  constructor(private httpClient: HttpClient, private authService: AuthService) {}

  makeNote(user: number, note: NoteDTO): Observable<NoteWithDateDTO> {
    return this.httpClient.post<NoteWithDateDTO>(`${this.url}/user/${user}/note`, note);
  }

}
