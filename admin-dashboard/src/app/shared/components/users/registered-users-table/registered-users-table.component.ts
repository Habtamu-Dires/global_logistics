import { Component } from '@angular/core';
import { PageResponseRegisteredUsers, RegisteredUsers } from '../../../../services/models';
import { Api } from '../../../../services/api';
import { getPageOfRegisteredUsers, verify, verifyPhone } from '../../../../services/functions';
import { HttpErrorResponse } from '@angular/common/http';
import { TimeUtilService } from '../../../../services/time-util/time-util.service';
import { ToastrService } from 'ngx-toastr';
import { BadgeComponent } from "../../ui/badge/badge.component";
import { ButtonComponent } from "../../ui/button/button.component";
import { TableDropdownComponent } from "../../common/table-dropdown/table-dropdown.component";

interface UserListItem {
  id:string,
  fullName:string,
  phone:string,
  role:string,
  status:string,
  createdAt:string
}


@Component({
  selector: 'app-registered-users-table',
  imports: [BadgeComponent, ButtonComponent, TableDropdownComponent],
  templateUrl: './registered-users-table.component.html',
  styles: ``
})
export class RegisteredUsersTableComponent {

  users: UserListItem[] = []
  usersRaw: RegisteredUsers[] = []

  currentPage = 1
  totalPages = 1
  pageSize = 10
  isLoading = false


  constructor(
    private api: Api,
    private toastr:ToastrService,
    private timeUtil:TimeUtilService
  ) {
    
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(page: number = 1) {
    this.isLoading = true

    this.api.invoke(getPageOfRegisteredUsers, {
      page: page - 1,
      size: this.pageSize
    })
    .then((res:PageResponseRegisteredUsers) => {
      if(res.content) {
        this.usersRaw = res.content

        this.users = res.content.map(a => this.mapToUserList(a))
      
        this.currentPage = Number(res.number ?? 0) + 1
        this.totalPages  = res.totalPages ?? 1
      }
    }).catch(err=>{
      if(err instanceof HttpErrorResponse){
        this.toastr.error(err.error.message);
      }
    })
    .finally(() => this.isLoading = false)
  }

  mapToUserList(user: RegisteredUsers): UserListItem {
    return {
      id: user.publicId as string,
      fullName: user.firstName + ' ' + user.lastName,
      phone: user.phone as string,
      role: user.roles?.at(0) as string,
      status:user.status as string,
      createdAt: this.timeUtil.formatWithMonthName(user.createdAt as string)
    }
  }

  verifyPhone(publicId:any){
    if(publicId){
      this.api.invoke(verifyPhone,{
        'public-id':publicId
      }).then(()=>{
        this.toastr.success("Successfully updated");
        this.loadUsers(this.currentPage);
      }).catch(err =>{
        if(err instanceof HttpErrorResponse){
          this.toastr.error(err.error.message);
        }
      })
    }
  }
  
  getBadgeColor(status: string) {
    switch (status) {
      case 'OTP_SENT': return 'warning'
      case 'VERFIED': return 'success'
      default: return 'warning'
    }
  }

  // pagination methods
  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return
    this.loadUsers(page)
  }


}
