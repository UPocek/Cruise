import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../../auth/services/auth.service";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root',
})
export class PasswordService {
  constructor(private httpClient: HttpClient, private authService: AuthService) {}
  changePassword(oldPass: string, newPass: string) {
    return this.httpClient.put(`${environment.urlBase}/user/${this.authService.getId()}/changePassword`, {new_password: newPass, old_password: oldPass})
  }
}
