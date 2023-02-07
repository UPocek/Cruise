import {PanicDTO} from "../../user/models/panic-dto";

export interface PanicsDTO {
  totalCount: number
  results: PanicDTO[]
}
