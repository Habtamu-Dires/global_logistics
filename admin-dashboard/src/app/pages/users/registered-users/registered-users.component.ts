import { Component } from '@angular/core';
import { RegisteredUsersTableComponent } from "../../../shared/components/users/registered-users-table/registered-users-table.component";
import { PageBreadcrumbComponent } from "../../../shared/components/common/page-breadcrumb/page-breadcrumb.component";

@Component({
  selector: 'app-registered-users',
  imports: [RegisteredUsersTableComponent, PageBreadcrumbComponent],
  templateUrl: './registered-users.component.html',
  styles:``
})
export class RegisteredUsersComponent {

}
