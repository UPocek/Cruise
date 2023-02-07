import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverMainPageComponent } from './driver-main-page.component';

describe('DriverMainPageComponent', () => {
  let component: DriverMainPageComponent;
  let fixture: ComponentFixture<DriverMainPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverMainPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverMainPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
