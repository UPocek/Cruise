import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { DriverCreate } from 'src/app/driver/models/driver_create-dto';
import { Driver } from 'src/app/driver/models/driver-dto';
import { License } from 'src/app/driver/models/license-dto';
import { Vehicle } from 'src/app/driver/models/vehicle-dto';
import { environment } from 'src/environments/environment';
import { RegisteredUserDTO } from 'src/app/user/models/registered-user-dto';

@Injectable({
  providedIn: 'root',
})
export class CreateDriverService {
  constructor(private httpClient: HttpClient) {}

  public getDriver(driverId: number): Observable<Driver> {
    return this.httpClient.get<Driver>(
      environment.urlBase + '/driver/' + driverId
    );
  }

  public createDriver(newDriver: DriverCreate): Observable<RegisteredUserDTO> {
    return this.httpClient.post<RegisteredUserDTO>(
      environment.urlBase + '/driver',
      newDriver
    );
  }

  public createVehicle(
    vehicle: Vehicle,
    driverId: number
  ): Observable<Vehicle> {
    return this.httpClient.post<Vehicle>(
      environment.urlBase + '/driver/' + driverId + '/vehicle',
      vehicle
    );
  }

  public createDocument(license: License, driverId: number) {
    return this.httpClient.post<License>(
      environment.urlBase + '/driver/' + driverId + '/documents',
      license
    );
  }
}
