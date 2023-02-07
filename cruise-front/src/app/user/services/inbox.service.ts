import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InboxService {
  chatInfo$ = new BehaviorSubject<[number | null, string | null]>([null, null]);
  adminChatInfo$ = new BehaviorSubject<[string | null, string | null, string | null, number | null]>([null, null, null, null]);

  showChat([rideId, type]: [number, string]) {
    this.chatInfo$.next([rideId, type]);
  }

  adminShowChat([name, surname, email, id]: [string, string, string, number]) {
    this.adminChatInfo$.next([name, surname, email, id]);
  }



  constructor() {}
}
