import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerDetailedHistoryComponent } from './passenger-detailed-history.component';

describe('PassengerDetailedHistoryComponent', () => {
  let component: PassengerDetailedHistoryComponent;
  let fixture: ComponentFixture<PassengerDetailedHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PassengerDetailedHistoryComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerDetailedHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
