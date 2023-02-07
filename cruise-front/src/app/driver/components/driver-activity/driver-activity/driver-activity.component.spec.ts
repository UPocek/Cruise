import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverActivityComponent } from './driver-activity.component';

describe('DriverActivityComponent', () => {
  let component: DriverActivityComponent;
  let fixture: ComponentFixture<DriverActivityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverActivityComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverActivityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
