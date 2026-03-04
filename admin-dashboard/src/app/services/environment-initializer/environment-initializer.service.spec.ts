import { TestBed } from '@angular/core/testing';

import { EnvironmentInitializerService } from './environment-initializer.service';

describe('EnvironmentInitializerService', () => {
  let service: EnvironmentInitializerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnvironmentInitializerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
