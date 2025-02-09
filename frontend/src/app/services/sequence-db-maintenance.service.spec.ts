import { TestBed } from '@angular/core/testing';

import { SequenceDbMaintenanceService } from './sequence-db-maintenance.service';

describe('SequenceDbMaintenanceService', () => {
  let service: SequenceDbMaintenanceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SequenceDbMaintenanceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
