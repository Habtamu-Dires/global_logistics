import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { activateDriver, addUserRemark, approveDriverProfile, getPageOfDrivers, rejectDriverProfile, suspendDriver } from '../../../../services/functions';
import { ToastrService } from 'ngx-toastr';
import { Api } from '../../../../services/api';
import { DriverProfileView, PageResponseDriverProfileView } from '../../../../services/models';
import { LabelComponent } from "../../form/label/label.component";
import { InputFieldComponent } from "../../form/input/input-field.component";
import { ButtonComponent } from "../../ui/button/button.component";
import { ResponsiveImageComponent } from "../../ui/images/responsive-image/responsive-image.component";
import { ModalComponent } from "../../ui/modal/modal.component";
import { BadgeComponent } from "../../ui/badge/badge.component";
import { TableDropdownComponent } from "../../common/table-dropdown/table-dropdown.component";
import { TimeUtilService } from '../../../../services/time-util/time-util.service';
import { ModalService } from '../../../services/modal.service';

interface DriverListItem {
  id?: string;
  status?: string;
  fullName?:string;
  phone?: string;
  licenceNumber?: string;
  preferredLanes?:string;
}


@Component({
  selector: 'app-drivers-table',
  imports: [LabelComponent, InputFieldComponent, ButtonComponent, ResponsiveImageComponent, ModalComponent, BadgeComponent, TableDropdownComponent],
  templateUrl: './drivers-table.component.html',
  styles:``
})
export class DriversTableComponent implements OnInit{
    
    drivers: DriverListItem[] = []
    driverRaw: DriverProfileView[] = [];
  
    selectedDriver:DriverProfileView | undefined;
  
    currentPage = 1
    totalPages = 1
    pageSize = 10
    isLoading = false
  
    isDetailModalOpen:boolean = false;
  
    constructor(
      private api:Api,
      private toastr:ToastrService,
      private timeUtil:TimeUtilService,
      private modal:ModalService
    ){}
  
    ngOnInit(): void {
      console.log("hello ")
      this.loadDrivers();
    }
  
    loadDrivers(page:number = 1){
      this.isLoading = true;
      this.api.invoke(getPageOfDrivers,{
        page: page - 1,
        size: this.pageSize
      }).then((res:PageResponseDriverProfileView) =>{
          if(res.content){
            this.driverRaw = res.content;
            
            this.drivers = res.content.map(c => this.mapToDriverListItem(c));
  
            this.currentPage = Number(res.number ?? 0) + 1
            this.totalPages  = res.totalPages ?? 1
          }
      }).catch(err=>{
        if(err instanceof HttpErrorResponse){
          this.toastr.error(err.error.message);
        }
        console.log(err);
      }).finally(()=> this.isLoading = false);
    }
    
    //apove consignor
    approve(publicId:any){
      if(publicId){
        this.api.invoke(approveDriverProfile,{
          'public-id': publicId
        }).then(()=>{
          this.toastr.success("Successfully update status");
          this.loadDrivers(this.currentPage);
          this.closeDetailModal();
        }).catch(err =>{
          if(err instanceof HttpErrorResponse){
            this.toastr.error(err.error.message);
          }
        });
      }
    } 
  
    // reject consignor
    reject(publicId:any){
      if(publicId){
        if(!this.selectedDriver?.remark){
          this.toastr.error("Remakr is Mandatory");
          return;
        }
        this.api.invoke(rejectDriverProfile,{
          'public-id':publicId ,
           body: {text: this.selectedDriver?.remark}
        }).then(()=>{
          this.toastr.success("Status Successfully Updated");
          this.loadDrivers(this.currentPage);
          this.closeDetailModal();
        }).catch(err => {
          if(err instanceof HttpErrorResponse){
            this.toastr.error(err.error.message);
          }
        })
      }
    }

    activate(publicId:any){
      if(publicId){
        this.api.invoke(activateDriver,{
          'public-id':publicId
        }).then(res =>{
          this.toastr.success("Status Successfully Updated");
          this.loadDrivers(this.currentPage);
          this.closeDetailModal();
        }).catch(err => {
          if(err instanceof HttpErrorResponse){
            this.toastr.error(err.error.message);
          }
        })
      }
    }

    // supend
    suspend(publicId:any){
      if(publicId){
        if(!this.selectedDriver?.remark){
          this.toastr.error("Remark is mandatory");
          return;
        }
        this.api.invoke(suspendDriver,{
          'public-id':publicId,
          body:{text: this.selectedDriver.remark}
        }).then(()=>{
          this.toastr.success("Successfully update status");
          this.loadDrivers(this.currentPage);
          this.closeDetailModal();
        }).catch(err =>{
            if(err instanceof HttpErrorResponse){
              this.toastr.error(err.error.message);
            }
        });
      }
    }
  
    // close detail modal
    closeDetailModal() {
      this.selectedDriver = undefined
      this.isDetailModalOpen = false
    }

    handleSave(){
      if(this.selectedDriver?.remark && this.selectedDriver?.publicId){
        this.api.invoke(addUserRemark,{
          'public-id': this.selectedDriver.publicId,
          body:{text: this.selectedDriver?.remark}
        }).then(()=>{
          this.closeDetailModal();
        }).catch(err =>{
          console.log(err);
          if(err instanceof HttpErrorResponse){
            this.toastr.error(err.error.message);
          }
        })
      }
    }
  
    // modal methods
    openDetailModal(id: string) {
      this.selectedDriver = this.driverRaw.find(a => a.publicId === id);
      if(this.selectedDriver){
        const createdAt = this.selectedDriver.createdAt as string;
        const updatedAt = this.selectedDriver.updatedAt as string;
        this.selectedDriver.createdAt = this.timeUtil.formatWithMonthName(createdAt)
        this.selectedDriver.updatedAt = this.timeUtil.formatWithMonthName(updatedAt);
      }
      this.isDetailModalOpen = true;
    }
   
  
    // helper method
    mapToDriverListItem(driver: DriverProfileView):DriverListItem{
      return {
          id: driver.publicId,
          status: driver.status,
          fullName:driver.firstName + ' ' + driver.lastName,
          phone: driver.phone,
          licenceNumber:driver.licenceNumber,
          preferredLanes:driver.preferredLanes,
      }
  
    }
  
    getBadgeColor(status: string) {
      switch (status) {
        case 'APPROVED': return 'success'
        case 'ACTIVE': return 'success'
        case 'SUSPENDED': return 'error';
        case 'REJECTED': return 'error'
        default: return 'warning'
      }
    }
  
    // pagination methods
    goToPage(page: number) {
      if (page < 1 || page > this.totalPages) return
      this.loadDrivers(page)
    }

    // time util

}
