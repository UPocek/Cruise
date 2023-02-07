import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverChangesNotificationComponent } from './driver-changes-notification.component';

describe('DriverChangesNotificationComponent', () => {
  let component: DriverChangesNotificationComponent;
  let fixture: ComponentFixture<DriverChangesNotificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverChangesNotificationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverChangesNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
