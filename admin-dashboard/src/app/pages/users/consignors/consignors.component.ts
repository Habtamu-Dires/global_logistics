import { Component } from '@angular/core';
import { PageBreadcrumbComponent } from "../../../shared/components/common/page-breadcrumb/page-breadcrumb.component";
import { AdminsTableComponent } from "../../../shared/components/users/admins-table/admin-table.component";
import { ConsignorsTableComponent } from "../../../shared/components/users/consignors-table/consignors-table.component";

@Component({
  selector: 'app-consignors',
  imports: [PageBreadcrumbComponent, ConsignorsTableComponent],
  templateUrl: './consignors.component.html',
  styles: ``
})
export class ConsignorsComponent {

}
