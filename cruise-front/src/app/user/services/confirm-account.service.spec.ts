import { TestBed } from '@angular/core/testing';

import { ConfirmAccountService } from './confirm-account.service';

describe('ConfirmAccountService', () => {
  let service: ConfirmAccountService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConfirmAccountService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
