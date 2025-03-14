import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduleStatisticsComponent } from './schedule-statistics.component';

describe('ScheduleStatisticsComponent', () => {
  let component: ScheduleStatisticsComponent;
  let fixture: ComponentFixture<ScheduleStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ScheduleStatisticsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduleStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
