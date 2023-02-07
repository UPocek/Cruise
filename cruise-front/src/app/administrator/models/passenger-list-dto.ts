import { UserDTO } from '../../user/models/user-dto';

export interface PassengerListDTO {
  totalCount: number;
  results: UserDTO[];
}
