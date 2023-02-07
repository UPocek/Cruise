import { Component, Input, OnInit } from '@angular/core';
import { PopUpService } from '../../services/pop-up.service';
import {
  trigger,
  state,
  style,
  animate,
  transition,
} from '@angular/animations';

@Component({
  selector: 'app-pop-up',
  templateUrl: './pop-up.component.html',
  styleUrls: ['./pop-up.component.css'],
  animations: [
    trigger('flyInOut', [
      state('in', style({ transform: 'translate(-50%, -16vw)' })),
      state('out', style({ transform: 'translate(-50%, 16vw)' })),
      transition('in => out', [animate('0.4s ease-in-out')]),
      transition('out => in', [animate('0.4s ease-in-out')]),
    ]),
  ],
})
export class PopUpComponent implements OnInit {
  popUpDurationInMilliseconds = 2500;
  showPopUp = false;
  @Input()
  errorMessage: string = '';

  constructor(private popUpService: PopUpService) {}

  ngOnInit(): void {
    this.popUpService.showPopUp$.subscribe((request) => {
      if (request !== '' && this.errorMessage == '') {
        this.errorMessage = request;
        this.showPopUp = true;
        this.hidePopUp();
        this.resetPopUp();
      }
    });
  }

  hidePopUp() {
    setTimeout(() => {
      this.showPopUp = false;
    }, this.popUpDurationInMilliseconds);
  }

  resetPopUp() {
    setTimeout(() => {
      this.errorMessage = '';
    }, this.popUpDurationInMilliseconds + 500);
  }
}
