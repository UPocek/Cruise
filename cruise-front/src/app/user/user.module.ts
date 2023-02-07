import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  MatFormFieldModule,
  MAT_FORM_FIELD_DEFAULT_OPTIONS,
} from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

import { RegistrationComponent } from './components/registration/registration.component';
import { CurrentRideComponent } from './components/current-ride/current-ride.component';
import { HistoryComponent } from './components/history/history.component';
import { InboxComponent } from './components/inbox/inbox.component';
import { PassengerMainPageComponent } from './components/passenger-main-page/passenger-main-page.component';
import { VerificationPendingComponent } from './components/verification-pending/verification-pending.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { NewPasswordComponent } from './components/new-password/new-password.component';
import { ResetPasswordPendingComponent } from './components/reset-password-pending/reset-password-pending.component';
import { AccountComponent } from './components/account/account.component';
import { ConfirmAccountComponent } from './components/confirm-account/confirm-account.component';
import { UniversalComponentsModule } from '../universal-components/universal-components.module';
import { CreateRideRequestComponent } from './components/create-ride-request/create-ride-request.component';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { EmailsResponseComponent } from './components/emails-response/emails-response.component';
import { RideInvitationComponent } from './components/ride-invitation/ride-invitation.component';
import { RideConfirmationComponent } from './components/ride-confirmation/ride-confirmation.component';
import { PassengerCurrentRideInfoComponent } from './components/passenger-current-ride-info/passenger-current-ride-info.component';
import { DriverModule } from '../driver/driver.module';
import { UserReportsComponent } from './components/user-reports/user-reports.component';
import { MaterialModule } from '../material.module';
import { NoCurrentRideComponent } from './components/no-current-ride/no-current-ride.component';
import { PassengerHistoryComponent } from './components/passenger-history/passenger-history.component';
import { PassengerDetailedHistoryComponent } from './components/passenger-detailed-history/passenger-detailed-history.component';
import { PassengerRideCardComponent } from './components/passenger-ride-card/passenger-ride-card.component';
import { ChatComponent } from './components/chat/chat.component';
import { InboxItemCardComponent } from './components/inbox-item-card/inbox-item-card.component';
import { InboxItemCardAdminComponent } from './components/inbox-item-card-admin/inbox-item-card-admin.component';

@NgModule({
  declarations: [
    RegistrationComponent,
    CurrentRideComponent,
    HistoryComponent,
    InboxComponent,
    PassengerMainPageComponent,
    VerificationPendingComponent,
    ConfirmAccountComponent,
    ResetPasswordComponent,
    NewPasswordComponent,
    ResetPasswordPendingComponent,
    AccountComponent,
    CreateRideRequestComponent,
    EmailsResponseComponent,
    RideInvitationComponent,
    RideConfirmationComponent,
    PassengerCurrentRideInfoComponent,
    UserReportsComponent,
    NoCurrentRideComponent,
    PassengerHistoryComponent,
    PassengerDetailedHistoryComponent,
    PassengerRideCardComponent,
    ChatComponent,
    InboxItemCardComponent,
    InboxItemCardAdminComponent,
  ],
  imports: [
    CommonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule,
    MatIconModule,
    RouterLink,
    RouterModule,
    MatButtonModule,
    UniversalComponentsModule,
    MatSelectModule,
    MatCheckboxModule,
    DriverModule,
    MaterialModule,
  ],
  exports: [],
  providers: [
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: { appearance: 'outline' },
    },
  ],
})
export class UserModule {}
