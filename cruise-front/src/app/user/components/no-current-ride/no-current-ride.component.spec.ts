import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoCurrentRideComponent } from './no-current-ride.component';

describe('NoCurrentRideComponent', () => {
  let component: NoCurrentRideComponent;
  let fixture: ComponentFixture<NoCurrentRideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NoCurrentRideComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NoCurrentRideComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
