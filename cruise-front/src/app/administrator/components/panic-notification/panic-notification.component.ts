import {Component, HostListener, Input, OnInit} from '@angular/core';
import {PanicDTO} from "../../../user/models/panic-dto";
import {AdminService} from "../../services/admin.service";
import {PanicService} from "../../services/panic.service";

@Component({
  selector: 'app-panic-notification',
  templateUrl: './panic-notification.component.html',
  styleUrls: ['./panic-notification.component.css']
})
export class PanicNotificationComponent implements OnInit {
  time: string = "nesto";
  @Input() panic!: PanicDTO;
  constructor(private adminService: AdminService, private panicService: PanicService) { }

  ngOnInit(): void {
    this.time = this.panic.time.split(".")[0];
  }

  @HostListener('click')
  clicked() {
    this.adminService.setNotificationForm("panic");
    this.panicService.setPanic(this.panic);
  }

}
