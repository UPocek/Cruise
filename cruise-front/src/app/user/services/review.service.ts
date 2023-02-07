import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ReviewDTO } from '../models/review-dto';
import { ReviewPairDTO } from '../models/review-pair-dto';
import { ReviewResponseDTO } from '../models/review-response-dto';

@Injectable({
  providedIn: 'root',
})
export class ReviewService {
  constructor(private http: HttpClient) {}

  leaveReviewForvehicle(
    review: ReviewDTO,
    rideId: number
  ): Observable<ReviewResponseDTO> {
    return this.http.post<ReviewResponseDTO>(
      `${environment.urlBase}/review/${rideId}/vehicle`,
      review
    );
  }

  leaveReviewForDriver(
    review: ReviewDTO,
    rideId: number
  ): Observable<ReviewResponseDTO> {
    return this.http.post<ReviewResponseDTO>(
      `${environment.urlBase}/review/${rideId}/driver`,
      review
    );
  }

  getAllUserReviewsForRide(rideId: number): Observable<ReviewPairDTO[]> {
    return this.http.get<ReviewPairDTO[]>(
      `${environment.urlBase}/review/${rideId}`
    );
  }

}
