import {DriverChangesDTO} from "./driver-changes-dto";
import {PanicDTO} from "../../user/models/panic-dto";

export interface AdminNotificationDTO {
  isPanic: boolean
  driverChanges: DriverChangesDTO
  panic: PanicDTO
  time: string
}
