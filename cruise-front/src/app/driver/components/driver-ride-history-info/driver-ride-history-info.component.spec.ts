import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverRideHistoryInfoComponent } from './driver-ride-history-info.component';

describe('DriverRideHistoryInfoComponent', () => {
  let component: DriverRideHistoryInfoComponent;
  let fixture: ComponentFixture<DriverRideHistoryInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverRideHistoryInfoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverRideHistoryInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
