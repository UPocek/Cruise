import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { VehicleDTO } from '../models/vehicle-dto';

@Injectable({
  providedIn: 'root',
})
export class MapService {
  route$ = new BehaviorSubject<google.maps.LatLngLiteral[] | null>(null);
  placedPins$ = new BehaviorSubject<string | null>(null);

  requestRoute(
    origin: google.maps.LatLngLiteral,
    destination: google.maps.LatLngLiteral
  ) {
    this.route$.next([origin, destination]);
  }

  resetRoutes() {
    this.route$.next(null);
  }

  addPinAddressToForm(address: string) {
    this.placedPins$.next(address);
  }

  constructor(private http: HttpClient) {}

  googleMapApiKey = environment.googleMapApiKey;
  private headers = new HttpHeaders({
    skip: 'true',
  });

  getLatLngFromAddress(address: string): Observable<any> {
    const url = `${environment.proxy}https://maps.googleapis.com/maps/api/geocode/json?address=${address}&key=${this.googleMapApiKey}`;
    return this.http.get(url, { headers: this.headers });
  }

  getAddressFromLatLng(lat: number, lng: number): Observable<any> {
    const url = `${environment.proxy}https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${lng}&key=${this.googleMapApiKey}`;
    return this.http.get(url, { headers: this.headers });
  }

  getAllActiveVehicles(): Observable<VehicleDTO[]> {
    return this.http.get<VehicleDTO[]>(
      `${environment.urlBase}/driver/all-active-vehicles`
    );
  }
}
