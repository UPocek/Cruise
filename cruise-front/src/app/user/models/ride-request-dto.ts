import { LocationPairDTO } from 'src/app/unregistered-user/models/location-pair-dto';
import { UserForRideDTO } from './user-for-ride-dto';

export interface RideRequestDTO {
  passengers: UserForRideDTO[];
  locations: LocationPairDTO[];
  vehicleType: string;
  babyTransport: boolean;
  petTransport: boolean;
  price: number;
  timeEstimation: number;
  distance: number;
  startTime: string;
}
