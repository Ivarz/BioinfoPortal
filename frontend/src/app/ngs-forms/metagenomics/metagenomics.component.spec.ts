import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MetagenomicsComponent } from './metagenomics.component';

describe('MetagenomicsComponent', () => {
  let component: MetagenomicsComponent;
  let fixture: ComponentFixture<MetagenomicsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MetagenomicsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MetagenomicsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
