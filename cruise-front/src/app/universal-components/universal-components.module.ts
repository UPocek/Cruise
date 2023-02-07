import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  MatFormFieldModule,
  MAT_FORM_FIELD_DEFAULT_OPTIONS,
} from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { RouterModule } from '@angular/router';
import { GoogleMapsModule } from '@angular/google-maps';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MapComponent } from '../universal-components/components/map/map.component';
import { AccountInfoComponent } from './components/account-info/account-info.component';
import { StarRatingComponent } from './components/star-rating/star-rating.component';
import { StopPropagationDirective } from './directives/stop-propagation.directive';
import { ChangePasswordDialog } from './components/account-info/dialog/change-password-dialog';
import { PopUpComponent } from './components/pop-up/pop-up.component';

@NgModule({
  declarations: [
    MapComponent,
    AccountInfoComponent,
    StarRatingComponent,
    ChangePasswordDialog,
    StopPropagationDirective,
    PopUpComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatFormFieldModule,
    RouterLink,
    RouterModule,
    MatInputModule,
    GoogleMapsModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    BrowserAnimationsModule,
  ],
  exports: [
    MapComponent,
    AccountInfoComponent,
    StarRatingComponent,
    StopPropagationDirective,
    PopUpComponent,
  ],
  providers: [
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: { appearance: 'outline' },
    },
  ],
})
export class UniversalComponentsModule {}
