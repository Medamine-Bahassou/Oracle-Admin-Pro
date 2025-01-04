import { TestBed } from '@angular/core/testing';

import { TableManagementServiceService } from './table-management-service.service';

describe('TableManagementServiceService', () => {
  let service: TableManagementServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TableManagementServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
