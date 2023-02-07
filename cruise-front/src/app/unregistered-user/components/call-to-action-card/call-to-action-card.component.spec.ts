import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CallToActionCardComponent } from './call-to-action-card.component';

describe('CallToActionCardComponent', () => {
  let component: CallToActionCardComponent;
  let fixture: ComponentFixture<CallToActionCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CallToActionCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CallToActionCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
