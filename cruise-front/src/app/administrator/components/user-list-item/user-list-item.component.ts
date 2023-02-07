import { Component, HostListener, Input, OnInit } from '@angular/core';
import { DriverChangesDTO } from '../../models/driver-changes-dto';
import { UserDTO } from '../../../user/models/user-dto';
import { UserInfoService } from '../../services/user-info.service';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';

@Component({
  selector: 'app-user-list-item',
  templateUrl: './user-list-item.component.html',
  styleUrls: ['./user-list-item.component.css'],
})
export class UserListItemComponent implements OnInit {
  @Input() userDTO?: UserDTO;
  firstname?: string = '';
  surname?: string = '';
  profilePicture?: any = 'assets/logo.png';
  @Input() passenger?: boolean = true;
  constructor(
    private userInfoService: UserInfoService,
    private popUpService: PopUpService
  ) {}

  ngOnInit(): void {
    if (this.userDTO) {
      this.firstname = this.userDTO.name;
      this.surname = this.userDTO.surname;
      this.profilePicture =
        this.userDTO?.profilePicture == 'data:image/jpeg;base64,'
          ? 'assets/logo.png'
          : this.userDTO?.profilePicture;
    } else {
      this.popUpService.showPopUp('User not assigned. 400 ERROR');
    }
  }
  @HostListener('click')
  clicked() {
    let el: any;
    if (this.passenger) el = document.getElementById('form');
    else el = document.getElementById('formDriver');
    el.style.visibility = 'visible';
    this.userInfoService.setProduct(this.userDTO);
  }
}
