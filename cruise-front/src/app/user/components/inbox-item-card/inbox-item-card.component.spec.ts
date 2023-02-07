import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboxItemCardComponent } from './inbox-item-card.component';

describe('InboxItemCardComponent', () => {
  let component: InboxItemCardComponent;
  let fixture: ComponentFixture<InboxItemCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboxItemCardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InboxItemCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
