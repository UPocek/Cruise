import { UserDTO } from '../../user/models/user-dto';

export interface DriverListDTO {
  totalCount: number;
  results: UserDTO[];
}
