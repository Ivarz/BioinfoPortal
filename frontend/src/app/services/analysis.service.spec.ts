import { TestBed } from '@angular/core/testing';

import { AnalysisResultsService } from './analysis.service';

describe('AnalysisResultsService', () => {
  let service: AnalysisService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AnalysisResultsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
