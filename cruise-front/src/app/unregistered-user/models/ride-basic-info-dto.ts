import { LocationPairDTO } from './location-pair-dto';

export interface RideInfoDTO {
  locations: LocationPairDTO[];
  vehicleType: string;
  babyTransport: boolean;
  petTransport: boolean;
}
