import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverChangesApprovalComponent } from './driver-changes-approval.component';

describe('DriverChangesApprovalComponent', () => {
  let component: DriverChangesApprovalComponent;
  let fixture: ComponentFixture<DriverChangesApprovalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverChangesApprovalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverChangesApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
