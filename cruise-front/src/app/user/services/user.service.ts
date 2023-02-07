import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { RideDTO } from '../models/ride-dto';
import { AuthService } from '../../auth/services/auth.service';
import { RegisteredUserDTO } from '../models/registered-user-dto';
import { AllChatItemsDTO } from '../models/all-chat-items-dto';
import { MessageDTO } from '../models/message-dto';
import { SendMessageDTO } from '../models/send-message-dto';
import {AllAdminChatItemsDTO} from "../models/admin-chat-dto";

@Injectable({
  providedIn: 'root',
})
export class UserService {
  url = `${environment.urlBase}`;

  private currentRide$ = new BehaviorSubject<RideDTO>(<RideDTO>{});
  selectedCurrentRide$ = this.currentRide$.asObservable();

  setCurrentRide(product: any) {
    this.currentRide$.next(product);
  }

  constructor(private http: HttpClient, private authService: AuthService) {}

  sendMessage(
    otherId: number,
    message: SendMessageDTO
  ): Observable<MessageDTO> {
    return this.http.post<MessageDTO>(
      `${this.url}/user/${otherId}/message`,
      message
    );
  }

  getUserByEmail(email: string): Observable<RegisteredUserDTO> {
    return this.http.get<RegisteredUserDTO>(`${this.url}/user/email/${email}`);
  }

  getAllUserChatItems(userId: number): Observable<AllChatItemsDTO> {
    return this.http.get<AllChatItemsDTO>(
      `${this.url}/user/${userId}/chat-items`
    );
  }
  getAllAdminChatItems(): Observable<AllAdminChatItemsDTO> {
    return this.http.get<AllAdminChatItemsDTO>(
      `${this.url}/admin/chat-items`
    );
  }

  getAllRideMessages(rideId: number): Observable<MessageDTO[]> {
    return this.http.get<MessageDTO[]>(
      `${this.url}/user/message/ride/${rideId}`
    );
  }

  getAllPanicMessages(rideId: number): Observable<MessageDTO[]> {
    return this.http.get<MessageDTO[]>(
      `${this.url}/user/message/panic/${rideId}`
    );
  }
  getAllSupportMessages(): Observable<MessageDTO[]> {
    return this.http.get<MessageDTO[]>(`${this.url}/user/message/support`);
  }

  getAllMessagesWithUser(id: number): Observable<MessageDTO[]> {
    return this.http.get<MessageDTO[]>(`${this.url}/admin/messageWith/${id}`)
  }
}
