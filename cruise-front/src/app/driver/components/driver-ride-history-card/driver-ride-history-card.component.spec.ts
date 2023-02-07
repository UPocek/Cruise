import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverRideHistoryCardComponent } from './driver-ride-history-card.component';

describe('DriverRideHistoryCardComponent', () => {
  let component: DriverRideHistoryCardComponent;
  let fixture: ComponentFixture<DriverRideHistoryCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverRideHistoryCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverRideHistoryCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
