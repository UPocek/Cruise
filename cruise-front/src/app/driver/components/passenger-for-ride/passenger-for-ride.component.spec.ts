import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerForRideComponent } from './passenger-for-ride.component';

describe('PassengerForRideComponent', () => {
  let component: PassengerForRideComponent;
  let fixture: ComponentFixture<PassengerForRideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PassengerForRideComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerForRideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
