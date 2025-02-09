import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmitNgsAnalysisComponent } from './submit-ngs-analysis.component';

describe('SubmitNgsAnalysisComponent', () => {
  let component: SubmitNgsAnalysisComponent;
  let fixture: ComponentFixture<SubmitNgsAnalysisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubmitNgsAnalysisComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubmitNgsAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
