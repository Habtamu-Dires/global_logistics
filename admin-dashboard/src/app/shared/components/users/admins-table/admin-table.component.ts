
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Api } from '../../../../services/api';
import { activateUser, addUserRemark, createAdminUser, disableUser, getPageOfAdmins } from '../../../../services/functions';
import { ToastrService } from 'ngx-toastr';
import { CreateAdminUserRequest, PageResponseUserProfile, UserProfile } from '../../../../services/models';
import { ButtonComponent } from "../../ui/button/button.component";
import { BadgeComponent } from "../../ui/badge/badge.component";
import { TableDropdownComponent } from "../../common/table-dropdown/table-dropdown.component";
import { LabelComponent } from "../../form/label/label.component";
import { ModalComponent } from "../../ui/modal/modal.component";
import { InputFieldComponent } from "../../form/input/input-field.component";
import { ModalService } from '../../../services/modal.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { GridShapeComponent } from "../../common/grid-shape/grid-shape.component";
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TimeUtilService } from '../../../../services/time-util/time-util.service';

interface AdminListItem {
  id?: string;
  fullName: string;
  phone: string;
  role: string;
  avatar?: string;
  status: string;
}

@Component({
  selector: 'app-admins-table',
  imports: [
    CommonModule,
    ButtonComponent,
    BadgeComponent,
    LabelComponent,
    ModalComponent,
    InputFieldComponent,
    GridShapeComponent,
    FormsModule,
    CommonModule
],
  templateUrl: './admins-table.component.html',
  styles: ``
})
export class AdminsTableComponent {

  isSuperAdmin:boolean = false;

  admins: AdminListItem[] = []
  adminsRaw: UserProfile[] = []

  currentPage = 1
  totalPages = 1
  pageSize = 10
  isLoading = false


  selectedAdmin: UserProfile | undefined;
  createAdminPayload: CreateAdminUserRequest = this.createEmptyAdminPayload();

  isCreateModalOpen = false
  isDetailModalOpen = false

  constructor(
    private api: Api,
    private toastr: ToastrService,
    private modal:ModalService,
    private authService:AuthService,
    private router:Router,
    private timeUtil:TimeUtilService
  ) {
    
  }

  ngOnInit(): void {
    this.isSuperAdmin = this.authService.isSuperAdmin;
    if(this.isSuperAdmin){
      this.loadAdmins();
    }
  }

  loadAdmins(page: number = 1) {
    this.isLoading = true

    this.api.invoke(getPageOfAdmins, {
      page: page - 1,
      size: this.pageSize
    })
    .then((res: PageResponseUserProfile) => {
      if(res.content) {
        this.adminsRaw = res.content

        this.admins = res.content.map(a => this.mapToAdminListItem(a))
      
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

  mapToAdminListItem(user: UserProfile): AdminListItem {
    return {
      id: user.publicId,
      fullName: user.firstName + ' ' + user.lastName,
      phone: user.phone,
      role: user.roles.includes('SUPER_ADMIN') ? 'Super Admin' : 'Admin',
      status: user.status,
      avatar: user.profilePic
    }
  }
  
  // activate user
  activateUser(adminId:any){
    if(adminId){
      this.api.invoke(activateUser,{
        'public-id':adminId
      }).then(res => {
        this.toastr.success("Status successfully updated");
        this.closeDetailModal();
        this.loadAdmins(this.currentPage);
      }).catch(err=>{
        this.toastr.error(err.error.message);
      })
    }
  }

  // disalbe user
  disableUser(adminId:any){
    if(adminId){
      if(!this.selectedAdmin?.remark){
        this.toastr.error("Remark is Mandatory");
        return;
      }
      this.api.invoke(disableUser,{
        'public-id':adminId,
        body:{text: this.selectedAdmin.remark} 
      }).then(()=>{
        this.toastr.success("Status successfully updated");
        this.loadAdmins(this.currentPage);
      }).catch(err => {
        this.toastr.error(err.error.message);
      })
      }
  }

  // open create admin modal
  openCreateAdminModal() {
    this.isDetailModalOpen = false
    this.isCreateModalOpen = true
    this.createAdminPayload = this.createEmptyAdminPayload();
  }

  // create admin
  createAdmin(payload: CreateAdminUserRequest) {
    this.api.invoke(createAdminUser, { body: payload })
      .then(() => {
        this.toastr.success('Admin created')
        this.isCreateModalOpen = false
        this.loadAdmins(1)
      })
      .catch(err =>{
        if(err instanceof HttpErrorResponse){
          this.toastr.error(err.error.message)
        }
      })
  }

  // on create admin form submit
  handleCreateAdmin() {
    if(this.createAdminPayload){
      console.log("Creating admin with payload: ", this.createAdminPayload);
      this.createAdmin(this.createAdminPayload);
    }
    this.modal.closeModal();
  }

  getBadgeColor(status: string) {
    switch (status) {
      case 'ACTIVE': return 'success'
      case 'DISABLED': return 'error'
      default: return 'warning'
    }
  }

  // modal methods
  openDetailModal(id: string) {
    this.selectedAdmin = this.adminsRaw.find(a => a.publicId === id);
    if(this.selectedAdmin){
        const createdAt = this.selectedAdmin.createdAt as string;
        const updatedAt = this.selectedAdmin.updatedAt as string;
        this.selectedAdmin.createdAt = this.timeUtil.formatWithMonthName(createdAt)
        this.selectedAdmin.updatedAt = this.timeUtil.formatWithMonthName(updatedAt);
    }
    this.isCreateModalOpen = false
    this.isDetailModalOpen = true
  }

  closeDetailModal() {
    this.selectedAdmin = undefined
    this.isDetailModalOpen = false
  }

  handleSave(){
    if(this.selectedAdmin?.remark){
      this.api.invoke(addUserRemark,{
        'public-id': this.selectedAdmin.publicId,
        body:{text: this.selectedAdmin?.remark}
      }).then(()=>{
        this.closeDetailModal();
      }).catch(err =>{
        if(err instanceof HttpErrorResponse){
          this.toastr.error(err.error.message);
        }
      })
    }
  }

  closeCreateModal() {
    this.isCreateModalOpen = false
  }

  // pagination methods
  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return
    this.loadAdmins(page)
  }

  // helper method to reset create admin form
  createEmptyAdminPayload(): CreateAdminUserRequest {
    return {
      firstName: '',
      lastName: '',
      phone: '',
      remark: ''
    }
  }

  backToHomePage(){
    this.router.navigate(['/']);
  }


}
