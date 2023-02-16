import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Stomp } from '@stomp/stompjs';
import { Subscription } from 'rxjs';
import SockJS from 'sockjs-client';
import { AuthService } from 'src/app/auth/services/auth.service';
import { PopUpService } from 'src/app/universal-components/services/pop-up.service';
import { environment } from 'src/environments/environment';
import { MessageDTO } from '../../models/message-dto';
import { SendMessageDTO } from '../../models/send-message-dto';
import { InboxService } from '../../services/inbox.service';
import { RideService } from '../../services/ride.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css'],
})
export class ChatComponent implements OnInit, OnDestroy {
  messages: MessageDTO[] = [];
  myId: number = -1;
  otherId: number = -1;
  otherPersonName: string = '';
  chatTime: string = '';
  chatType: string = '';
  rideId: number = -1;

  url = environment.serverUrl;
  isLoaded = false;
  stompClient: any;
  chatSubscription: Subscription = new Subscription();
  adminChatSubscription: Subscription = new Subscription();
  messagingSubscription: any;

  messageForm = new FormGroup({
    messageInput: new FormControl('', [Validators.required]),
  });

  constructor(
    private inboxService: InboxService,
    private rideService: RideService,
    private userSerice: UserService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private popUpService: PopUpService
  ) {}

  ngOnInit(): void {
    this.myId = this.authService.getId();
    this.route.params.subscribe((parameters) => {
      const rideId = +parameters['id'];
      if (rideId != null) {
        this.displayChat(rideId, 'RIDE');
      }
    });
    this.chatSubscription = this.inboxService.chatInfo$.subscribe(
      ([rideId, type]) => {
        if (rideId != null && type != null) {
          this.displayChat(rideId, type);
        }
      }
    );
    this.adminChatSubscription = this.inboxService.adminChatInfo$.subscribe(
      ([name, surname, email, id]) => {
        if (name != null && surname != null && email != null && id != null) {
          this.getMessagesForAdmin(name, surname, email, id);
        }
      }
    );
  }

  displayChat(rideId: number, type: string) {
    this.reset();
    if (type === 'RIDE') {
      this.chatType = 'RIDE';
      this.getMessagesForRide(rideId);
    } else if (type === 'PANIC') {
      this.chatType = 'PANIC';
      this.getMessagesForPanic(rideId);
    } else if (type === 'SUPPORT') {
      this.chatType = 'SUPPORT';
      this.getMessagesWithSupport();
    }
  }

  ngOnDestroy(): void {
    this.chatSubscription.unsubscribe();
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  }

  reset() {
    this.messages = [];
    this.otherId = -1;
    this.rideId = -1;
    this.otherPersonName = '';
    this.chatTime = '';
    this.chatType = '';
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  }

  startListeningForMessages() {
    let that = this;

    this.stompClient = Stomp.over(function () {
      return new SockJS(`${that.url}/socket`);
    });

    this.stompClient.connect({}, function () {
      that.isLoaded = true;
      that.openSocket();
    });
  }

  openSocket() {
    if (this.isLoaded) {
      this.messagingSubscription = this.stompClient.subscribe(
        '/socket-out/chat/' + this.rideId + '/' + this.myId,
        (message: any) => {
          this.messages.push(JSON.parse(message.body));
        }
      );
    }
  }

  sendMessage() {
    let messageToSend: SendMessageDTO;
    if (this.authService.getRole() === 'ROLE_ADMIN') {
      messageToSend = {
        message: this.messageForm.value.messageInput!,
        type: 'SUPPORT',
        rideId: -1,
      };
    } else {
      messageToSend = {
        message: this.messageForm.value.messageInput!,
        type: this.chatType,
        rideId: this.rideId,
      };
    }
    this.userSerice.sendMessage(this.otherId, messageToSend).subscribe({
      next: (message) => {
        this.messages.push(message);
      },
      error: (error: HttpErrorResponse) => {
        this.popUpService.showPopUp(error.message);
      },
    });
    this.messageForm.reset();
  }

  getMessagesForRide(rideId: number) {
    this.rideService.getRideById(rideId).subscribe({
      next: (rideInfo) => {
        this.rideId = rideInfo.id;
        this.otherId =
          rideInfo.driver?.id == this.myId
            ? rideInfo.passengers[0].id
            : rideInfo.driver!.id;
        this.otherPersonName =
          rideInfo.driver?.id == this.myId
            ? rideInfo.passengers[0].email
            : rideInfo.driver!.email;
        const messageDateTime = rideInfo.startTime.split('.')[0].split('T');
        const messageDateTokens = messageDateTime[0].split('-');
        const messageTimeTokens = messageDateTime[1];
        this.chatTime = `${messageDateTokens[2]}.${messageDateTokens[1]}.${messageDateTokens[0]} ${messageTimeTokens}`;
        this.startListeningForMessages();
      },
    });
    this.userSerice.getAllRideMessages(rideId).subscribe({
      next: (rideMessages) => {
        this.messages = rideMessages;
      },
    });
  }

  getMessagesForPanic(rideId: number) {
    this.rideService.getRideById(rideId).subscribe({
      next: (rideInfo) => {
        this.rideId = rideInfo.id;
        this.otherId =
          rideInfo.driver?.id == this.myId
            ? rideInfo.passengers[0].id
            : rideInfo.driver!.id;
        this.otherPersonName =
          rideInfo.driver?.id == this.myId
            ? rideInfo.passengers[0].email
            : rideInfo.driver!.email;
        const messageDateTime = rideInfo.startTime.split('.')[0].split('T');
        const messageDateTokens = messageDateTime[0].split('-');
        const messageTimeTokens = messageDateTime[1];
        this.chatTime = `${messageDateTokens[2]}.${messageDateTokens[1]}.${messageDateTokens[0]} ${messageTimeTokens}`;
        this.startListeningForMessages();
      },
    });
    this.userSerice.getAllPanicMessages(rideId).subscribe({
      next: (panicMessages) => {
        this.messages = panicMessages;
      },
    });
  }

  getMessagesWithSupport() {
    this.otherId = 1;
    this.otherPersonName = 'Support';
    const messageDateTime = new Date().toISOString().split('.')[0].split('T');
    const messageDateTokens = messageDateTime[0].split('-');
    const messageTimeTokens = messageDateTime[1];
    this.chatTime = `${messageDateTokens[2]}.${messageDateTokens[1]}.${messageDateTokens[0]} ${messageTimeTokens}`;
    this.startListeningForMessages();
    this.userSerice.getAllSupportMessages().subscribe({
      next: (supportMessages) => {
        this.messages = supportMessages;
      },
    });
  }

  getMessagesForAdmin(
    name: string,
    surname: string,
    email: string,
    id: number
  ) {
    this.otherId = id;
    this.otherPersonName = name + ' ' + surname;
    const messageDateTime = new Date().toISOString().split('.')[0].split('T');
    const messageDateTokens = messageDateTime[0].split('-');
    const messageTimeTokens = messageDateTime[1];
    this.chatTime = `${messageDateTokens[2]}.${messageDateTokens[1]}.${messageDateTokens[0]} ${messageTimeTokens}`;
    this.startListeningForMessages();
    this.userSerice.getAllMessagesWithUser(id).subscribe({
      next: (supportMessages) => {
        this.messages = supportMessages;
      },
    });
  }
}
