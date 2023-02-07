import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  MatFormFieldModule,
  MAT_FORM_FIELD_DEFAULT_OPTIONS,
} from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { RouterLink } from '@angular/router';
import { RouterModule } from '@angular/router';

import { RideEstimationComponent } from './components/ride-estimation/ride-estimation.component';
import { CallToActionCardComponent } from './components/call-to-action-card/call-to-action-card.component';
import { StartPageComponent } from './components/start-page/start-page.component';
import { UniversalComponentsModule } from '../universal-components/universal-components.module';

@NgModule({
  declarations: [
    RideEstimationComponent,
    CallToActionCardComponent,
    StartPageComponent,
  ],
  imports: [
    CommonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule,
    UniversalComponentsModule,
    RouterLink,
    RouterModule,
  ],
  providers: [
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: { appearance: 'outline' },
    },
  ],
})
export class UnregisteredUserModule {}
