import {UserDTO} from "./user-dto";
import {RideDTO} from "./ride-dto";

export interface PanicDTO {
  id: number;
  user: UserDTO;
  ride: RideDTO;
  time: string;
  reason: string;
}
