import { AfterContentInit, Component, OnDestroy, OnInit } from '@angular/core';
import { DriverInfoChangesService } from '../../services/driver-info-changes.service';
import { AdminNotificationDTO } from '../../models/admin-notification-dto';
import { PanicService } from '../../services/panic.service';
import { AdminService } from '../../services/admin.service';
import * as SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { AuthService } from '../../../auth/services/auth.service';
import { Subscription } from 'rxjs';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-admin-notifications',
  templateUrl: './admin-notifications.component.html',
  styleUrls: ['./admin-notifications.component.css'],
})
export class AdminNotificationsComponent
  implements OnInit, AfterContentInit, OnDestroy
{
  list: AdminNotificationDTO[] = [];
  notificationForm: string = '';
  url = environment.serverUrl;
  isLoaded = false;
  private stompClient: any;
  driverChangesSubscription: Subscription = new Subscription();
  panicsSubscription: Subscription = new Subscription();

  constructor(
    private driverInfoChangesService: DriverInfoChangesService,
    private panicService: PanicService,
    private adminService: AdminService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.initializeWebSocketConnection();
    this.notificationForm = '';
    this.list = [];
    this.driverChangesSubscription = this.driverInfoChangesService
      .getAllRequests()
      .subscribe((response) => {
        for (let i = 0; i < response.length; i++) {
          this.list.push(<AdminNotificationDTO>{
            isPanic: false,
            driverChanges: response[i],
            time: response[i].time,
          });
        }
        this.sortList();
      });

    this.panicsSubscription = this.panicService
      .getPanics()
      .subscribe((response) => {
        for (let i = 0; i < response.totalCount; i++)
          this.list.push(<AdminNotificationDTO>(<unknown>{
            isPanic: true,
            panic: response.results[i],
            time: response.results[i].time.replace(' ', 'T'),
          }));
        this.sortList();
      });

    this.adminService.selectedNotificationForm$.subscribe((response) => {
      this.notificationForm = response;
    });
  }

  initializeWebSocketConnection() {
    let that = this;

    this.stompClient = Stomp.over(function () {
      return new SockJS(`${that.url}/socket`);
    });

    this.stompClient.connect({}, function () {
      that.isLoaded = true;
      that.openSocket();
    });
  }

  openSocket() {
    if (this.isLoaded) {
      this.stompClient.subscribe(
        '/socket-out/panic/' + this.authService.getId(),
        (panic: any) => {
          this.list.push(JSON.parse(panic.body));
          this.sortList();
        }
      );
      this.stompClient.subscribe(
        '/socket-out/driverChanges/' + this.authService.getId(),
        (changes: any) => {
          this.list.push(JSON.parse(changes.body));
          this.sortList();
        }
      );
    }
  }

  ngAfterContentInit() {
    this.notificationForm = '';
  }

  sortList() {
    this.list = this.list.sort((a, b) => (a.time < b.time ? 1 : -1));
  }

  ngOnDestroy(): void {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
    this.driverChangesSubscription.unsubscribe();
    this.panicsSubscription.unsubscribe();
  }
}
