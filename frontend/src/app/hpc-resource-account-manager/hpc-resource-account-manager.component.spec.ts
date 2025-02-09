import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpcResourceAccountManagerComponent } from './hpc-resource-account-manager.component';

describe('HpcResourceAccountManagerComponent', () => {
  let component: HpcResourceAccountManagerComponent;
  let fixture: ComponentFixture<HpcResourceAccountManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HpcResourceAccountManagerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpcResourceAccountManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
