import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { RouterModule } from '@angular/router';

import { DriverMainPageComponent } from './components/driver-main-page/driver-main-page.component';
import { DriverActivityComponent } from './components/driver-activity/driver-activity/driver-activity.component';
import { DriverRideRequestComponent } from './components/driver-ride-request/driver-ride-request.component';
import { MaterialModule } from '../material.module';
import { UniversalComponentsModule } from '../universal-components/universal-components.module';
import { DriverCurrentRideInfoComponent } from './components/driver-current-ride-info/driver-current-ride-info.component';
import { PassengerForRideComponent } from './components/passenger-for-ride/passenger-for-ride.component';
import { DriverRideHistoryCardComponent } from './components/driver-ride-history-card/driver-ride-history-card.component';
import { DriverRideHistoryInfoComponent } from './components/driver-ride-history-info/driver-ride-history-info.component';

@NgModule({
  declarations: [
    DriverMainPageComponent,
    DriverActivityComponent,
    DriverRideRequestComponent,
    DriverCurrentRideInfoComponent,
    PassengerForRideComponent,
    DriverRideHistoryCardComponent,
    DriverRideHistoryInfoComponent,
  ],
  imports: [
    CommonModule,
    MatSlideToggleModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule,
    MatIconModule,
    MaterialModule,
    UniversalComponentsModule,
    RouterLink,
    RouterModule,
  ],
  exports: [
    DriverCurrentRideInfoComponent,
    DriverRideHistoryCardComponent,
    DriverRideHistoryInfoComponent,
  ],
})
export class DriverModule {}
