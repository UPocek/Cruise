import { LocationPairDTO } from 'src/app/unregistered-user/models/location-pair-dto';
import { UserForRideDTO } from './user-for-ride-dto';

export interface FavouriteRideDTO {
  id: number;
  favoriteName: string;
  locations: LocationPairDTO[];
  passengers: UserForRideDTO[];
  vehicleType: string;
  babyTransport: boolean;
  petTransport: boolean;
}
