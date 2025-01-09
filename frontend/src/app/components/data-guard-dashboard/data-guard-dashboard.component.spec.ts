import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataGuardDashboardComponent } from './data-guard-dashboard.component';

describe('DataGuardDashboardComponent', () => {
  let component: DataGuardDashboardComponent;
  let fixture: ComponentFixture<DataGuardDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DataGuardDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DataGuardDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
