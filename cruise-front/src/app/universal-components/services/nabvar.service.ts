import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NabvarService {
  private role$ = new BehaviorSubject<string>('');
  selectedRole$ = this.role$.asObservable();

  passengerItems: string[] = [
    'Main',
    'Current ride',
    'History',
    'Inbox',
    'Account',
    'Log out',
  ];
  passengerLinks: string[] = [
    'passenger-main',
    'current-ride',
    'passenger-history',
    'inbox',
    'account',
    'log-in',
  ];

  driverItems: string[] = [
    'Main',
    'Current ride',
    'History',
    'Inbox',
    'Account',
    'Log out',
  ];
  driverLinks: string[] = [
    'driver-main',
    'current-ride',
    'history',
    'inbox',
    'account',
    'log-in',
  ];

  adminItems: string[] = [
    'Main',
    'Users',
    'Create driver',
    'History',
    'Account',
    'Notifications',
    'Inbox',
    'Log out',
  ];
  adminLinks: string[] = [
    'admin-main',
    'users-list',
    'create-driver',
    'history',
    'account',
    'admin-notifications',
    'inbox',
    'log-in',
  ];

  unregisterdUserItems: string[] = ['Home', 'Log in', 'Sign up'];
  unregisterdUserLinks: string[] = ['home', 'log-in', 'sign-up'];
  constructor() {}

  setRole(role: string) {
    this.role$.next(role);
  }

  getPassengerNavbar(): string[] {
    return this.passengerItems;
  }

  getDriverNavbar(): string[] {
    return this.driverItems;
  }

  getAdminNavbar(): string[] {
    return this.adminItems;
  }

  getUnregisteredUserNavbar(): string[] {
    return this.unregisterdUserItems;
  }

  getPassengerLinks(): string[] {
    return this.passengerLinks;
  }

  getDriverLinks(): string[] {
    return this.driverLinks;
  }

  getAdminLinks(): string[] {
    return this.adminLinks;
  }

  getUnregisteredUserLinks(): string[] {
    return this.unregisterdUserLinks;
  }
}
