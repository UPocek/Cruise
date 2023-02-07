import { UserForRideDTO } from './user-for-ride-dto';

export interface ReviewResponseDTO {
  id: number;
  rating: number;
  comment: string;
  passenger: UserForRideDTO;
  reviewFor: string;
}
