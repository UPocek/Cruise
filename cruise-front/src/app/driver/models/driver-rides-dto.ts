import {RideDTO} from "../../user/models/ride-dto";

export interface DriverRidesDTO {
  totalCount: number
  results: RideDTO[]
}
