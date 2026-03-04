import { Component } from '@angular/core';
import { PageBreadcrumbComponent } from "../../../shared/components/common/page-breadcrumb/page-breadcrumb.component";
import { AdminsTableComponent } from "../../../shared/components/users/admins-table/admin-table.component";
import { ComponentCardComponent } from "../../../shared/components/common/component-card/component-card.component";

@Component({
  selector: 'app-admins',
  imports: [PageBreadcrumbComponent, AdminsTableComponent, ComponentCardComponent],
  templateUrl: './admins.component.html',
  styles: ``,
})
export class AdminsComponent {

}
