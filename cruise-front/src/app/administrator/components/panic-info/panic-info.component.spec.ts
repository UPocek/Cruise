import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanicInfoComponent } from './panic-info.component';

describe('PanicInfoComponent', () => {
  let component: PanicInfoComponent;
  let fixture: ComponentFixture<PanicInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PanicInfoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PanicInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
