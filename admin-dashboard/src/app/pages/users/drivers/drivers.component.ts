import { Component } from '@angular/core';
import { PageBreadcrumbComponent } from "../../../shared/components/common/page-breadcrumb/page-breadcrumb.component";
import { ConsignorsTableComponent } from "../../../shared/components/users/consignors-table/consignors-table.component";
import { DriversTableComponent } from "../../../shared/components/users/drivers-table/drivers-table.component";

@Component({
  selector: 'app-drivers',
  imports: [PageBreadcrumbComponent, DriversTableComponent],
  templateUrl: './drivers.component.html',
  styles: ``,
})
export class DriversComponent {

}
