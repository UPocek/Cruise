import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpHeaders,
  HttpErrorResponse,
  HttpClient,
} from '@angular/common/http';
import { catchError, Observable, switchMap, throwError } from 'rxjs';

import { PopUpService } from 'src/app/universal-components/services/pop-up.service';
import { AuthService } from '../services/auth.service';
import { LoginDTO } from '../models/login-dto';
import { environment } from 'src/environments/environment';

@Injectable()
export class Interceptor implements HttpInterceptor {
  private isRefreshing = false;

  constructor(
    private authService: AuthService,
    private httpClient: HttpClient,
    private popUpService: PopUpService
  ) {}
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const accessToken: any = localStorage.getItem('jwt');
    if (req.headers.get('skip')) {
      if (req.headers.get('ggl')) {
        req = req.clone({
          headers: req.headers.delete('skip').delete('ggl'),
        });
      }
      return next.handle(req);
    }

    const headers_object = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + accessToken,
    });

    const cloned = req.clone({
      headers: headers_object,
    });

    return next.handle(cloned).pipe(
      catchError((error) => {
        if (
          error instanceof HttpErrorResponse &&
          error.error.message != undefined &&
          error.error.message.split(' ')[0] === '401'
        ) {
          return this.handle401Error(req, next);
        }

        return throwError(error);
      })
    );
  }
  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      const token = localStorage.getItem('refreshToken');
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
          skip: 'true',
        }),
      };
      if (token)
        return this.httpClient
          .post<LoginDTO>(
            `${environment.urlBase}/user/refreshToken',`,
            <LoginDTO>{
              accessToken: localStorage.getItem('jwt'),
              refreshToken: localStorage.getItem('refreshToken'),
            },
            httpOptions
          )
          .pipe(
            switchMap((token) => {
              this.isRefreshing = false;
              localStorage.setItem('jwt', token.accessToken);
              localStorage.setItem('refreshToken', token.refreshToken);
              return next.handle(
                this.addTokenHeader(request, token.accessToken)
              );
            }),
            catchError((err) => {
              this.isRefreshing = false;
              this.popUpService.showPopUp(
                'Your session has expired, please log in again!'
              );
              this.authService.logout();
              return throwError(err);
            })
          );
    }

    return next.handle(
      this.addTokenHeader(request, localStorage.getItem('jwt'))
    );
  }

  private addTokenHeader(request: HttpRequest<any>, token: unknown) {
    const accessToken: any = localStorage.getItem('jwt');

    const headers_object = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + accessToken,
    });
    return request.clone({
      headers: headers_object,
    });
  }
}
