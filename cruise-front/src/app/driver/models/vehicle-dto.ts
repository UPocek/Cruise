import { CurrentLocation } from './current_location-dto';

export interface Vehicle {
  vehicleType: string;
  model: string;
  licenseNumber: string;
  currentLocation: CurrentLocation;
  passengerSeats: number;
  babyTransport: boolean;
  petTransport: boolean;
}
