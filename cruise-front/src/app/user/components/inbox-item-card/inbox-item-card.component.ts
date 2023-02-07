import { Component, Input } from '@angular/core';
import { ChatItemDTO } from '../../models/chat-item-dto';
import { InboxService } from '../../services/inbox.service';

@Component({
  selector: 'app-inbox-item-card',
  templateUrl: './inbox-item-card.component.html',
  styleUrls: ['./inbox-item-card.component.css'],
})
export class InboxItemCardComponent {
  constructor(private inboxService: InboxService) {}

  @Input()
  chatItemContent?: ChatItemDTO;

  openChat() {
    if (this.chatItemContent) {
      this.inboxService.showChat([
        this.chatItemContent.rideId,
        this.chatItemContent.type,
      ]);
    }
  }
}
