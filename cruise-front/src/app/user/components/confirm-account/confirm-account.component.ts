import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConfirmAccountService } from '../../services/confirm-account.service';

@Component({
  selector: 'app-confirm-account',
  templateUrl: './confirm-account.component.html',
  styleUrls: ['./confirm-account.component.css'],
})
export class ConfirmAccountComponent implements OnInit {
  status: RequestStatus = RequestStatus.PENDING;

  constructor(
    private service: ConfirmAccountService,
    private route: ActivatedRoute
  ) {}

  verificationId = -1;

  ngOnInit(): void {
    this.route.params.subscribe((parameters) => {
      this.verificationId = +parameters['activationId'];
      this.verifyUser(this.verificationId);
    });
  }

  verifyUser(id: number) {
    this.service.verifyUser(id).subscribe({
      next: (response) => {
        this.status = RequestStatus.VERIFIED;
      },
      error: (err: HttpErrorResponse) => {
        if (err.status == 200) {
          this.status = RequestStatus.VERIFIED;
        }
        this.status = RequestStatus.DENIED;
      },
    });
  }
}

enum RequestStatus {
  PENDING = 0,
  VERIFIED = 1,
  DENIED = 2,
}
