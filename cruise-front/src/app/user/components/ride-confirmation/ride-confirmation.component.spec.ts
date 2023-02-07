import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideConfirmationComponent } from './ride-confirmation.component';

describe('RideConfirmationComponent', () => {
  let component: RideConfirmationComponent;
  let fixture: ComponentFixture<RideConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RideConfirmationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
