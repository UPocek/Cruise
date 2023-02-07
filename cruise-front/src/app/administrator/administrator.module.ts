import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DriverFormComponent } from './components/driver-form/driver-form.component';
import { MatInputModule } from '@angular/material/input';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  MatFormFieldModule,
  MAT_FORM_FIELD_DEFAULT_OPTIONS,
} from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatStepperModule } from '@angular/material/stepper';
import { AdminMainPageComponent } from './components/admin-main-page/admin-main-page.component';
import { DriverChangesApprovalComponent } from './components/driver-changes-approval/driver-changes-approval.component';
import { AdminNotificationsComponent } from './components/admin-notifications/admin-notifications.component';
import { DriverChangesNotificationComponent } from './components/driver-changes-notification/driver-changes-notification.component';
import { MaterialModule } from '../material.module';
import { UsersListComponent } from './components/users-list/users-list.component';
import { UserInfoComponent } from './components/user-info/user-info.component';
import { UserListItemComponent } from './components/user-list-item/user-list-item.component';
import { PanicNotificationComponent } from './components/panic-notification/panic-notification.component';
import { PanicInfoComponent } from './components/panic-info/panic-info.component';
import { UniversalComponentsModule } from '../universal-components/universal-components.module';
import {NotesDialog} from "./components/user-info/dialog/notes-dialog";

@NgModule({
  declarations: [
    DriverFormComponent,
    AdminMainPageComponent,
    DriverChangesApprovalComponent,
    AdminNotificationsComponent,
    DriverChangesNotificationComponent,
    UsersListComponent,
    UserInfoComponent,
    UserListItemComponent,
    PanicNotificationComponent,
    PanicInfoComponent,
    NotesDialog
  ],
  exports: [DriverFormComponent],
  providers: [
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: { appearance: 'outline' },
    },
  ],
  imports: [
    CommonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule,
    MatIconModule,
    RouterLink,
    MatButtonModule,
    MatSelectModule,
    MatCheckboxModule,
    MatStepperModule,
    MaterialModule,
    UniversalComponentsModule,
  ],
})
export class AdministratorModule {}
