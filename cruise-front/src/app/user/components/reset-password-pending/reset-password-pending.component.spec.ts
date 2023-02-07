import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResetPasswordPendingComponent } from './reset-password-pending.component';

describe('ResetPasswordPendingComponent', () => {
  let component: ResetPasswordPendingComponent;
  let fixture: ComponentFixture<ResetPasswordPendingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ResetPasswordPendingComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResetPasswordPendingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
