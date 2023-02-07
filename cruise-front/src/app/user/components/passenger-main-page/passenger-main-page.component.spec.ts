import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerMainPageComponent } from './passenger-main-page.component';

describe('PassengerMainPageComponent', () => {
  let component: PassengerMainPageComponent;
  let fixture: ComponentFixture<PassengerMainPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PassengerMainPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerMainPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
