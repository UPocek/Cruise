import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRideRequestComponent } from './create-ride-request.component';

describe('CreateRideRequestComponent', () => {
  let component: CreateRideRequestComponent;
  let fixture: ComponentFixture<CreateRideRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateRideRequestComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateRideRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
