import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerRideCardComponent } from './passenger-ride-card.component';

describe('PassengerRideCardComponent', () => {
  let component: PassengerRideCardComponent;
  let fixture: ComponentFixture<PassengerRideCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PassengerRideCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerRideCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
