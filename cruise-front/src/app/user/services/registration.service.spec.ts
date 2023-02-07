import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

import { RegistrationService } from './registration.service';
import { RegisteredUserDTO } from '../models/registered-user-dto';
import { UserDTO } from '../models/user-dto';
import { environment } from 'src/environments/environment';
import { HttpErrorResponse } from '@angular/common/http';

fdescribe('RegistrationService', () => {
  let service: RegistrationService;
  let httpMockController: HttpTestingController;

  const validFormInputs = <UserDTO>{
    name: 'Mock',
    surname: 'Mockovic',
    profilePicture: 'data:image/jpeg;base64, ',
    telephoneNumber: '0238802388',
    email: 'mock@gmail.com',
    address: 'Mock adresa',
    password: 'mock',
  };

  const invalidFormInputs = <UserDTO>{
    name: 'M',
    surname: 'Mockovic',
    profilePicture: 'data:image/jpeg;base64, ',
    telephoneNumber: '0238802388',
    email: 'mock@gmail.com',
    address: 'Mock adresa',
    password: 'mock',
  };

  const errorMessageIfDateInvalid = 'name must have from 2 to 20 characters';

  const registeredUser = <RegisteredUserDTO>{
    id: 1,
    name: 'Mock',
    surname: 'Mockovic',
    email: 'mock@gmail.com',
    address: 'Mock adresa',
    profilePicture: 'data:image/jpeg;base64, ',
    telephoneNumber: '0238802388',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [RegistrationService],
    });
  });

  beforeEach(() => {
    service = TestBed.inject(RegistrationService);
    httpMockController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return registered user', () => {
    service.registerUser(validFormInputs).subscribe((response) => {
      expect(response).toEqual(registeredUser);
    });

    const request = httpMockController.expectOne(
      `${environment.urlBase}/passenger`
    );

    expect(request.request.method).toEqual('POST');

    request.flush(registeredUser);

    httpMockController.verify();
  });

  it('should return registered user', () => {
    service.registerUser(invalidFormInputs).subscribe({
      next: () => {},
      error: (error: HttpErrorResponse) => {
        expect(error.status).withContext('status').toEqual(400);
        expect(error.error)
          .withContext('message')
          .toEqual(errorMessageIfDateInvalid);
      },
    });

    const request = httpMockController.expectOne(
      `${environment.urlBase}/passenger`
    );

    expect(request.request.method).toEqual('POST');

    request.flush(errorMessageIfDateInvalid, {
      status: 400,
      statusText: 'Bed Request',
    });
  });
});
