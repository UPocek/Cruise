import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PopUpService {
  constructor() {}

  showPopUp$ = new BehaviorSubject<string>('');

  showPopUp(errorMessageToShow: string) {
    this.showPopUp$.next(errorMessageToShow);
  }
}
