import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideEstimationComponent } from './ride-estimation.component';

describe('RideEstimationComponent', () => {
  let component: RideEstimationComponent;
  let fixture: ComponentFixture<RideEstimationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RideEstimationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideEstimationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
