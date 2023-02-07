import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Token } from '@angular/compiler';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';

import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private headers = new HttpHeaders({
    'Content-Type': 'application/json',
    skip: 'true',
  });

  user$ = new BehaviorSubject<string>('');
  userState$ = this.user$.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.user$.next(this.getRole());
  }

  login(auth: any): Observable<Token> {
    return this.http.post<Token>(environment.urlBase + 'log-in', auth, {
      headers: this.headers,
    });
  }

  logout() {
    window.localStorage.clear();
    this.setUser();
    this.router.navigate(['log-in']);
  }

  getRole(): any {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('jwt');
      const helper = new JwtHelperService();
      return helper.decodeToken(accessToken).role;
    }
    return null;
  }

  getEmail(): any {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('jwt');
      const helper = new JwtHelperService();
      return helper.decodeToken(accessToken).sub;
    }
    return null;
  }
  getId(): any {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('jwt');
      const helper = new JwtHelperService();
      return helper.decodeToken(accessToken).id;
    }
    return null;
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('jwt') != null;
  }

  setUser(): void {
    this.user$.next(this.getRole());
  }
}
