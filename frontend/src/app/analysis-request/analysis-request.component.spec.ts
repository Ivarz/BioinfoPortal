import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalysisResultsComponent } from './analysis-results.component';

describe('AnalysisResultsComponent', () => {
  let component: AnalysisResultsComponent;
  let fixture: ComponentFixture<AnalysisResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnalysisResultsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AnalysisResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
