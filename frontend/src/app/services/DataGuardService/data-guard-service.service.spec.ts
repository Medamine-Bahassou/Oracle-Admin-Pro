import { TestBed } from '@angular/core/testing';

import { DataGuardServiceService } from './data-guard-service.service';

describe('DataGuardServiceService', () => {
  let service: DataGuardServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataGuardServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
