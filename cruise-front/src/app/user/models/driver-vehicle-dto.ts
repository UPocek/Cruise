import { LocationDTO } from '../../unregistered-user/models/location-dto';

export interface DriverVehicleDTO {
  id: number;
  driverId: number;
  model: string;
  vehicleType: string;
  licenceNumber: string;
  passengerSeats:number;
  currentLocation: LocationDTO;
  babyTransport: boolean;
  petTransport: boolean;
}
