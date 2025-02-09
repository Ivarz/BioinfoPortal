import { TestBed } from '@angular/core/testing';

import { SubmittedSamplesService } from './submitted-samples.service';

describe('SubmittedSamplesService', () => {
  let service: SubmittedSamplesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubmittedSamplesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
