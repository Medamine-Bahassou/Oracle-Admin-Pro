import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SlowQueriesComponent } from './slow-queries.component';

describe('SlowQueriesComponent', () => {
  let component: SlowQueriesComponent;
  let fixture: ComponentFixture<SlowQueriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SlowQueriesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SlowQueriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
