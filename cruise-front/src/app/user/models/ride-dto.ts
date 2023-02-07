import { LocationPairDTO } from 'src/app/unregistered-user/models/location-pair-dto';
import { RejectionDTO } from './rejection-dto';
import { UserForRideDTO } from './user-for-ride-dto';

export interface RideDTO {
  passengers: UserForRideDTO[];
  locations: LocationPairDTO[];
  vehicleType: string;
  babyTransport: boolean;
  petTransport: boolean;
  estimatedTimeInMinutes: number;
  status: string;
  id: number;
  startTime: string;
  endTime: string;
  totalCost: number;
  driver: UserForRideDTO | null;
  rejection: RejectionDTO | null;
  distance: number;
}
