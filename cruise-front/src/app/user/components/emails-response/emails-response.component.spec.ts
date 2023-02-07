import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailsResponseComponent } from './emails-response.component';

describe('EmailsResponseComponent', () => {
  let component: EmailsResponseComponent;
  let fixture: ComponentFixture<EmailsResponseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmailsResponseComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmailsResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
