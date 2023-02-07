import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverRideRequestComponent } from './driver-ride-request.component';

describe('DriverRideRequestComponent', () => {
  let component: DriverRideRequestComponent;
  let fixture: ComponentFixture<DriverRideRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverRideRequestComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverRideRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
