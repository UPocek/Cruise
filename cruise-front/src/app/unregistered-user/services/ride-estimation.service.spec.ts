import { TestBed } from '@angular/core/testing';

import { RideEstimationService } from './ride-estimation.service';

describe('RideEstimationService', () => {
  let service: RideEstimationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RideEstimationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
