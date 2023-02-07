import { UserForRideDTO } from './user-for-ride-dto';

export interface ReviewDTO {
  id: number;
  rating: number;
  comment: string;
  passenger: UserForRideDTO;
  reviewFor: string;
}
