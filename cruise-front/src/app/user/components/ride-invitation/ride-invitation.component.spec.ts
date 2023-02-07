import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideInvitationComponent } from './ride-invitation.component';

describe('RideInvitationComponent', () => {
  let component: RideInvitationComponent;
  let fixture: ComponentFixture<RideInvitationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RideInvitationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideInvitationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
