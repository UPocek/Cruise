import { Component, OnInit } from '@angular/core';
import { PanicDTO } from '../../../user/models/panic-dto';
import { PanicService } from '../../services/panic.service';

@Component({
  selector: 'app-panic-info',
  templateUrl: './panic-info.component.html',
  styleUrls: ['./panic-info.component.css'],
})
export class PanicInfoComponent implements OnInit {
  panic!: PanicDTO;
  message!: string;
  time!: string;
  constructor(private panicService: PanicService) {}

  ngOnInit(): void {
    this.panicService.selectedPanic$.subscribe((response) => {
      this.panic = response;
      this.message = response.reason;
      this.time = response.time.split(' ')[1].split('.')[0];
    });
  }
}
