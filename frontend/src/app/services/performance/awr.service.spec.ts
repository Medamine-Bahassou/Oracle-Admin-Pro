import { TestBed } from '@angular/core/testing';

import { AwrService } from './awr.service';

describe('AwrService', () => {
  let service: AwrService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AwrService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
