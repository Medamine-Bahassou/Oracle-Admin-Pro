import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableManagementComponent } from './table-management-component.component'; // Fixed import

describe('TableManagementComponent', () => { // Fixed name in describe block
  let component: TableManagementComponent; // Fixed name here
  let fixture: ComponentFixture<TableManagementComponent>; // Fixed name here

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TableManagementComponent] // Fixed name here
    })
      .compileComponents();

    fixture = TestBed.createComponent(TableManagementComponent); // Fixed name here
    component = fixture.componentInstance; // Fixed name here
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
