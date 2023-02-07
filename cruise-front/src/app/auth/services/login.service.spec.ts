import { TestBed } from '@angular/core/testing';

import { LoginService } from './login.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {environment} from "../../../environments/environment";
import {CredentialsDTO} from "../models/credentials-dto";
import {LoginDTO} from "../models/login-dto";

fdescribe('LoginService', () => {
  let service: LoginService;
  let httpMockController: HttpTestingController;

  let credentials = <CredentialsDTO> {
    email: 'mock@gmail.com',
    password: 'mock'
  }
  let badCredentials = <CredentialsDTO> {
    email: 'mock@gmail.com',
    password: 'm'
  }

  let loginDTO = <LoginDTO> {
    accessToken: 'mock',
    refreshToken: 'MOCK'
  }

  let blockedEmail = "mockonja@gmail.com"
  let notBlockedEmail = "mock@gmail.com"


  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LoginService]
    });
    service = TestBed.inject(LoginService);
    httpMockController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return logged user token', () => {
    service.loginUser(credentials).subscribe((response) => {
      expect(response).toEqual(loginDTO)
    })

    const request = httpMockController.expectOne(
      `${environment.urlBase}/user/login`
    );

    expect(request.request.method).toEqual('POST');

    request.flush(loginDTO);

    httpMockController.verify();
  })

  it('should return null', () => {
    service.loginUser(badCredentials).subscribe((response) => {
      // @ts-ignore
      expect(response).toEqual(null)
    })

    const request = httpMockController.expectOne(
      `${environment.urlBase}/user/login`
    );

    expect(request.request.method).toEqual('POST');

    request.flush(null);

    httpMockController.verify();
  })

  it('should return true user is blocked', () => {
    service.getUser(blockedEmail).subscribe((response) => {
      expect(response).toEqual(true)
    })

    const request = httpMockController.expectOne(
      `${environment.urlBase}/user/isBlocked/${blockedEmail}`
    );

    expect(request.request.method).toEqual('GET');

    request.flush(true);

    httpMockController.verify();
  })

  it('should return false user is not blocked', () => {
    service.getUser(notBlockedEmail).subscribe((response) => {
      expect(response).toEqual(false)
    })

    const request = httpMockController.expectOne(
      `${environment.urlBase}/user/isBlocked/${notBlockedEmail}`
    );

    expect(request.request.method).toEqual('GET');

    request.flush(false);

    httpMockController.verify();
  })
});
