import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverCurrentRideInfoComponent } from './driver-current-ride-info.component';

describe('DriverCurrentRideInfoComponent', () => {
  let component: DriverCurrentRideInfoComponent;
  let fixture: ComponentFixture<DriverCurrentRideInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverCurrentRideInfoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverCurrentRideInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
