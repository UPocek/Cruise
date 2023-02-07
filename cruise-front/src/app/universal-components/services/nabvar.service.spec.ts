import { TestBed } from '@angular/core/testing';

import { NabvarService } from './nabvar.service';

describe('NabvarService', () => {
  let service: NabvarService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NabvarService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
