import { TestBed } from '@angular/core/testing';

import { RideRequestService } from './ride-request.service';

describe('RideRequestService', () => {
  let service: RideRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RideRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
