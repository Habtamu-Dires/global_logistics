import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShipmentWorkspaceComponent } from './shipment-workspace.component';

describe('ShipmentWorkspaceComponent', () => {
  let component: ShipmentWorkspaceComponent;
  let fixture: ComponentFixture<ShipmentWorkspaceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShipmentWorkspaceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShipmentWorkspaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
