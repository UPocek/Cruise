import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { AuthService } from 'src/app/auth/services/auth.service';
import { environment } from 'src/environments/environment';
import { AnswerEmailDTO } from '../../models/answer-email-dto';
@Component({
  selector: 'app-ride-invitation',
  templateUrl: './ride-invitation.component.html',
  styleUrls: ['./ride-invitation.component.css'],
})
export class RideInvitationComponent implements OnInit, OnDestroy {
  @Input() invitation!: AnswerEmailDTO;
  @Output() invitationResponded = new EventEmitter<AnswerEmailDTO>();

  isLoaded = false;
  private stompClient: any;
  url = environment.serverUrl;
  passengerId!: number;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.passengerId = this.authService.getId();
    this.initializeWebSocketConnenction();
  }

  ngOnDestroy(): void {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
  }

  initializeWebSocketConnenction() {
    let that = this;

    this.stompClient = Stomp.over(function () {
      return new SockJS(`${that.url}/socket`);
    });

    this.stompClient.connect({}, function () {
      that.isLoaded = true;
    });
  }

  sendResponseMessageUsingSocket(answer: AnswerEmailDTO) {
    this.stompClient.send(
      '/socket-in/email-invitation',
      {},
      JSON.stringify(answer)
    );
  }

  respondToInvitation(response: string) {
    this.invitation.answer = response;
    this.sendResponseMessageUsingSocket(this.invitation);
    this.invitationResponded.emit(this.invitation);
  }
}
