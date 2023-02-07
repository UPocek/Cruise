import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboxItemCardAdminComponent } from './inbox-item-card-admin.component';

describe('InboxItemCardAdminComponent', () => {
  let component: InboxItemCardAdminComponent;
  let fixture: ComponentFixture<InboxItemCardAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InboxItemCardAdminComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InboxItemCardAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
