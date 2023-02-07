import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

import { UserDTO } from '../../user/models/user-dto';
import { RegisteredUserDTO } from '../../user/models/registered-user-dto';
import { environment } from 'src/environments/environment';
import { WorkingHourDTO } from 'src/app/driver/models/working-hour-dto';

import { RideDTO } from '../../user/models/ride-dto';
import { DriverVehicleDTO } from '../../user/models/driver-vehicle-dto';
import { DriverChangesDTO } from '../../administrator/models/driver-changes-dto';
import { DriverRidesDTO } from '../models/driver-rides-dto';
import { WorkintTimeDTO } from '../models/working-time-dto';

@Injectable({
  providedIn: 'root',
})
export class DriverService {
  private driverActivity$ = new BehaviorSubject<boolean>(false);
  currentDriverActivity$ = this.driverActivity$.asObservable();

  private ride$ = new BehaviorSubject<RideDTO>(<RideDTO>{});
  historyRide = this.ride$.asObservable();

  private driverRide$ = new BehaviorSubject<RideDTO>(<RideDTO>{});
  currentDriverRide$ = this.driverRide$.asObservable();

  rideIdsOfAnsweredRideRequests: number[] = [];

  addRideIdOfNewAnswer(rideId: number) {
    this.rideIdsOfAnsweredRideRequests.push(rideId);
  }

  setHistoryRide(status: RideDTO) {
    this.ride$.next(status);
  }

  setDriverRide(status: RideDTO) {
    this.driverRide$.next(status);
  }

  driverUrlExtension = '/driver/';

  constructor(private httpClient: HttpClient) {}

  getDriver(email: string): Observable<RegisteredUserDTO> {
    return this.httpClient.get<RegisteredUserDTO>(
      `${environment.urlBase}${this.driverUrlExtension}email=${email}`
    );
  }

  getDriversVehicle(id: number): Observable<DriverVehicleDTO> {
    return this.httpClient.get<DriverVehicleDTO>(
      `${environment.urlBase}${this.driverUrlExtension}${id}/vehicle`
    );
  }
  getDriversWorkingTime(id: number): Observable<WorkintTimeDTO> {
    return this.httpClient.get<WorkintTimeDTO>(
      `${environment.urlBase}${this.driverUrlExtension}${id}/workingTime`
    );
  }

  requestChanges(id: string, user: UserDTO): Observable<DriverChangesDTO> {
    return this.httpClient.post<DriverChangesDTO>(
      `${environment.urlBase}${this.driverUrlExtension}requestChanges/${id}`,
      user
    );
  }

  activateDriver(driverId: number, status: boolean): Observable<boolean> {
    return this.httpClient.put<boolean>(
      `${environment.urlBase}${this.driverUrlExtension}${driverId}/activate?activityStatus=${status}`,
      null
    );
  }

  checkForRidesAssignedToDriver(): Observable<RideDTO> {
    return this.httpClient.get<RideDTO>(
      `${environment.urlBase}${this.driverUrlExtension}assignedRides`
    );
  }

  getAllRides(
    id: number,
    page: number,
    size: number,
    sort: string,
    from: string,
    to: string
  ): Observable<DriverRidesDTO> {
    return this.httpClient.get<DriverRidesDTO>(
      `${environment.urlBase}${this.driverUrlExtension}${id}/ride?page=${page}&size=${size}&sort=${sort}&from=${from}&to=${to}`
    );
  }

  logOutDriver(driverId: number) {
    this.activateDriver(driverId, false).subscribe();
  }
}
