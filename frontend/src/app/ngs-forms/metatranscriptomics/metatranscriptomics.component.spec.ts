import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MetatranscriptomicsComponent } from './metatranscriptomics.component';

describe('MetatranscriptomicsComponent', () => {
  let component: MetatranscriptomicsComponent;
  let fixture: ComponentFixture<MetatranscriptomicsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MetatranscriptomicsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MetatranscriptomicsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
