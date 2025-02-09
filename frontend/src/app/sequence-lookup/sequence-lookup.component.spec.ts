import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SequenceLookupComponent } from './sequence-lookup.component';

describe('SequenceLookupComponent', () => {
  let component: SequenceLookupComponent;
  let fixture: ComponentFixture<SequenceLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SequenceLookupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SequenceLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
