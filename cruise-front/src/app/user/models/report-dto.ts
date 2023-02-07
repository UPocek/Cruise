import { DayDTO } from './day-dto';

export interface ReportDTO {
  title: string;
  label: string;
  sum: number;
  avg: number;
  days: DayDTO[];
}
