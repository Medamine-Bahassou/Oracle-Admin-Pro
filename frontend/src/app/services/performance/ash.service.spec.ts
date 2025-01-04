import { TestBed } from '@angular/core/testing';

import { AshService } from './ash.service';

describe('AshService', () => {
  let service: AshService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AshService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
