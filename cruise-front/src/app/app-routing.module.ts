import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CurrentRideComponent } from './user/components/current-ride/current-ride.component';
import { HistoryComponent } from './user/components/history/history.component';
import { InboxComponent } from './user/components/inbox/inbox.component';
import { PassengerMainPageComponent } from './user/components/passenger-main-page/passenger-main-page.component';
import { RegistrationComponent } from './user/components/registration/registration.component';
import { DriverFormComponent } from './administrator/components/driver-form/driver-form.component';
import { LoginComponent } from './auth/login/login.component';
import { VerificationPendingComponent } from './user/components/verification-pending/verification-pending.component';
import { ConfirmAccountComponent } from './user/components/confirm-account/confirm-account.component';
import { DriverMainPageComponent } from './driver/components/driver-main-page/driver-main-page.component';
import { AdminMainPageComponent } from './administrator/components/admin-main-page/admin-main-page.component';
import { ResetPasswordComponent } from './user/components/reset-password/reset-password.component';
import { NewPasswordComponent } from './user/components/new-password/new-password.component';
import { ResetPasswordPendingComponent } from './user/components/reset-password-pending/reset-password-pending.component';
import { AccountComponent } from './user/components/account/account.component';
import { AdminNotificationsComponent } from './administrator/components/admin-notifications/admin-notifications.component';
import { StartPageComponent } from './unregistered-user/components/start-page/start-page.component';
import { LoginGuard } from './auth/guard/login.guard';
import { UsersListComponent } from './administrator/components/users-list/users-list.component';
import { DriverRideRequestComponent } from './driver/components/driver-ride-request/driver-ride-request.component';
import { EmailsResponseComponent } from './user/components/emails-response/emails-response.component';
import { RideConfirmationComponent } from './user/components/ride-confirmation/ride-confirmation.component';
import { PassengerHistoryComponent } from './user/components/passenger-history/passenger-history.component';
import { UserReportsComponent } from './user/components/user-reports/user-reports.component';
import { AdminGuard } from "./auth/guard/admin.guard";
import { DriverGuard } from "./auth/guard/driver.guard";
import { PassengerGuard } from "./auth/guard/passenger-guard";

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  {
    path: 'home',
    component: StartPageComponent,
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('../app/auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'create-driver',
    component: DriverFormComponent,
    canActivate: [AdminGuard],
    loadChildren: () =>
      import('../app/auth/auth.module').then((m) => m.AuthModule),
  },
  { path: 'sign-up', component: RegistrationComponent },
  {
    path: 'log-in',
    component: LoginComponent,
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('../app/auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'passenger-main',
    component: PassengerMainPageComponent,
    canActivate: [PassengerGuard],
    loadChildren: () =>
      import('../app/auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'driver-main',
    component: DriverMainPageComponent,
    canActivate: [DriverGuard],
    loadChildren: () =>
      import('../app/auth/auth.module').then((m) => m.AuthModule),
  },
  { path: 'admin-main',
    component: AdminMainPageComponent,
    canActivate: [AdminGuard],
    loadChildren: () =>
      import('../app/auth/auth.module').then((m) => m.AuthModule),
  },
  { path: 'account', component: AccountComponent },
  { path: 'current-ride', component: CurrentRideComponent },
  { path: 'history', component: HistoryComponent },
  { path: 'inbox', component: InboxComponent },
  { path: 'inbox/:id', component: InboxComponent },
  {
    path: 'passenger-history',
    component: PassengerHistoryComponent,
    canActivate: [PassengerGuard],
    loadChildren: () =>
      import('../app/auth/auth.module').then((m) => m.AuthModule),
  },
  { path: 'verification-pending', component: VerificationPendingComponent },
  { path: 'confirm-account/:activationId', component: ConfirmAccountComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'new-password/:id/:code', component: NewPasswordComponent },
  { path: 'reset-password-pending', component: ResetPasswordPendingComponent },
  {
    path: 'admin-notifications',
    component: AdminNotificationsComponent,
    canActivate: [AdminGuard],
    loadChildren: () =>
      import('../app/auth/auth.module').then((m) => m.AuthModule),
  },
  { path: 'users-list', component: UsersListComponent },
  { path: 'driver-ride-request', component: DriverRideRequestComponent },
  { path: 'email-response', component: EmailsResponseComponent },
  { path: 'ride-confirmation', component: RideConfirmationComponent },
  { path: 'reports', component: UserReportsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
