import { TestBed } from '@angular/core/testing';

import { CreateDriverService } from './create-driver.service';

describe('CreateDriverService', () => {
  let service: CreateDriverService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CreateDriverService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
