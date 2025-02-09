import { TestBed } from '@angular/core/testing';

import { HpcAccountService } from './hpc-account.service';

describe('HpcAccountService', () => {
  let service: HpcAccountService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HpcAccountService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
