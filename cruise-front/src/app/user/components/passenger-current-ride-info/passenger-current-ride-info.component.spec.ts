import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerCurrentRideInfoComponent } from './passenger-current-ride-info.component';

describe('PassengerCurrentRideInfoComponent', () => {
  let component: PassengerCurrentRideInfoComponent;
  let fixture: ComponentFixture<PassengerCurrentRideInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PassengerCurrentRideInfoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerCurrentRideInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
