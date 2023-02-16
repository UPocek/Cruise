import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { GoogleMapsModule } from '@angular/google-maps';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { UserModule } from './user/user.module';
import { NavbarComponent } from './universal-components/components/navbar/navbar.component';
import { AdministratorModule } from './administrator/administrator.module';
import { DriverModule } from './driver/driver.module';
import { UnregisteredUserModule } from './unregistered-user/unregistered-user.module';
import { UniversalComponentsModule } from './universal-components/universal-components.module';
import { AuthModule } from './auth/auth.module';
import { Interceptor } from './auth/interceptor/interceptor.interceptor';

@NgModule({
  declarations: [AppComponent, NavbarComponent],
  imports: [
    HttpClientModule,
    BrowserModule,
    BrowserAnimationsModule,
    GoogleMapsModule,
    AppRoutingModule,
    UserModule,
    AuthModule,
    AdministratorModule,
    DriverModule,
    UnregisteredUserModule,
    UniversalComponentsModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: Interceptor,
      multi: true,
    },
    { provide: LocationStrategy, useClass: HashLocationStrategy },
  ],
  exports: [AppComponent, NavbarComponent],
  bootstrap: [AppComponent],
})
export class AppModule {}
