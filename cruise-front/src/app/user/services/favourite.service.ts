import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { FavouriteRideBasicDTO } from '../models/favourite-ride-basic-dto';
import { FavouriteRideDTO } from '../models/favourite-ride-dto';

@Injectable({
  providedIn: 'root',
})
export class FavouriteService {
  constructor(private http: HttpClient) {}

  allFavouriteRides$ = new BehaviorSubject<FavouriteRideDTO[]>([]);

  setFavouriteRides(newFavourites: FavouriteRideDTO[]) {
    this.allFavouriteRides$.next(newFavourites);
  }

  addNewFavouriteRide(newFavourite: FavouriteRideDTO) {
    let favourites = this.allFavouriteRides$.value;
    favourites?.push(newFavourite);
    this.setFavouriteRides(favourites);
  }

  removeFavouriteRide(removedFavouriteRide: FavouriteRideDTO) {
    const favourites = this.allFavouriteRides$.value;
    const index = favourites.indexOf(removedFavouriteRide);
    if (index > -1) {
      favourites.splice(index, 1);
    }
    this.setFavouriteRides(favourites);
  }

  getAllUserFavouriteRides(): Observable<FavouriteRideDTO[]> {
    return this.http.get<FavouriteRideDTO[]>(
      `${environment.urlBase}/ride/favourites`
    );
  }

  addRideToFavourites(
    favouriteRideBasicDTO: FavouriteRideBasicDTO
  ): Observable<FavouriteRideDTO> {
    return this.http.post<FavouriteRideDTO>(
      `${environment.urlBase}/ride/favourites`,
      favouriteRideBasicDTO
    );
  }

  removeRideFromFavourites(favouruteId: number): Observable<any> {
    return this.http.delete<any>(
      `${environment.urlBase}/ride/favourites/${favouruteId}`
    );
  }
}
