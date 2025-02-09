import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TranscriptomicsComponent } from './transcriptomics.component';

describe('TranscriptomicsComponent', () => {
  let component: TranscriptomicsComponent;
  let fixture: ComponentFixture<TranscriptomicsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TranscriptomicsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TranscriptomicsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
