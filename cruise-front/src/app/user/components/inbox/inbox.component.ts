import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/services/auth.service';
import { ChatItemDTO } from '../../models/chat-item-dto';
import { UserService } from '../../services/user.service';
import {UserForAdminChatDTO} from "../../models/UserForAdminChatDTO";

@Component({
  selector: 'app-inbox',
  templateUrl: './inbox.component.html',
  styleUrls: ['./inbox.component.css'],
})
export class InboxComponent implements OnInit {
  constructor(
    private userService: UserService,
    private authService: AuthService
  ) {}
  userId: number = -1;
  chatItems: ChatItemDTO[] = [];
  adminChatItems: UserForAdminChatDTO[] = [];
  supportContent: ChatItemDTO = {
    departureAddress: 'Support',
    destinationAddress: '',
    rideId: -1,
    type: 'SUPPORT',
    rideStatus: '',
  };

  showSupport: boolean = this.authService.getRole() !== 'ROLE_ADMIN';



  ngOnInit(): void {
    this.userId = this.authService.getId();
    if(!this.showSupport)
    {
      this.userService.getAllAdminChatItems().subscribe({
        next: (chats) => {
          this.adminChatItems = chats.users;
        },
        error: () => {}
      })
    }
    else
    {
      this.userService.getAllUserChatItems(this.userId).subscribe({
        next: (chatItemResults) => {
          this.chatItems = chatItemResults.chatItems;
        },
        error: () => {
        },
      });
    }
  }
}
