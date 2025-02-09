import { TestBed } from '@angular/core/testing';

import { RoleMappingService } from './role-mapping.service';

describe('RoleMappingService', () => {
  let service: RoleMappingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RoleMappingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
