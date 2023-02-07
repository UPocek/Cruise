import { LocationDTO } from '../../unregistered-user/models/location-dto';

export interface VehicleDTO {
  id: number;
  driverId: number;
  currentLocation: LocationDTO;
  status: string;
}
