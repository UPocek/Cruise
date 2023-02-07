import { Component, Input } from '@angular/core';
import { InboxService } from '../../services/inbox.service';
import { UserForAdminChatDTO } from '../../models/UserForAdminChatDTO';

@Component({
  selector: 'app-inbox-item-card-admin',
  templateUrl: './inbox-item-card-admin.component.html',
  styleUrls: ['./inbox-item-card-admin.component.css'],
})
export class InboxItemCardAdminComponent {
  constructor(private inboxService: InboxService) {}

  @Input()
  chatItemContent?: UserForAdminChatDTO;

  openChat() {
    if (this.chatItemContent) {
      this.inboxService.adminShowChat([
        this.chatItemContent.name,
        this.chatItemContent.surname,
        this.chatItemContent.email,
        this.chatItemContent.id,
      ]);
    }
  }
}
