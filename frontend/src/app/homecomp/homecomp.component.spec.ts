import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomecompComponent } from './homecomp.component';

describe('HomecompComponent', () => {
  let component: HomecompComponent;
  let fixture: ComponentFixture<HomecompComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomecompComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomecompComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
