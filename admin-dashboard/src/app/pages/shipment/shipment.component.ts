import { Component } from '@angular/core';
import { PageBreadcrumbComponent } from "../../shared/components/common/page-breadcrumb/page-breadcrumb.component";
import { ShipmentsComponent } from "../../shared/components/shipment/shipment-table/shipment-table.component";

@Component({
  selector: 'app-shipment',
  imports: [PageBreadcrumbComponent, ShipmentsComponent],
  templateUrl: './shipment.component.html',
  styles: ``,
})
export class ShipmentComponent {

}
