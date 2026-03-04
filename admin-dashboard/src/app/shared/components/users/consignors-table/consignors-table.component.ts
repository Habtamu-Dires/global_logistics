import { Component, OnInit } from '@angular/core';
import { Api } from '../../../../services/api';
import { activateConsignorProfile, addUserRemark, approveConsignorProfile, getPageOfConsignors, rejectConsignorProfile, suspendConsignorProfile } from '../../../../services/functions';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { BadgeComponent } from "../../ui/badge/badge.component";
import { TableDropdownComponent } from "../../common/table-dropdown/table-dropdown.component";
import { ButtonComponent } from "../../ui/button/button.component";
import { LabelComponent } from "../../form/label/label.component";
import { InputFieldComponent } from "../../form/input/input-field.component";
import { ModalComponent } from "../../ui/modal/modal.component";
import { ResponsiveImageComponent } from "../../ui/images/responsive-image/responsive-image.component";
import { ConsignorProfileView, PageResponseConsignorProfileView } from '../../../../services/models';
import { TimeUtilService } from '../../../../services/time-util/time-util.service';

interface ConsignorListItem {
  id?: string;
  status?: string;
  fullName?:string;
  phone?: string;
  tradeLicence?: string;
  businessName?: string;
}


@Component({
  selector: 'app-consignors-table',
  imports: [BadgeComponent, TableDropdownComponent, ButtonComponent, LabelComponent, 
    InputFieldComponent, ModalComponent, ResponsiveImageComponent
  ],
  templateUrl: './consignors-table.component.html',
  styles: ``,
})
export class ConsignorsTableComponent implements OnInit {

  consignors: ConsignorListItem[] = []
  consignorRaw: ConsignorProfileView[] = [];

  selectedConsignor:ConsignorProfileView | undefined;

  currentPage = 1
  totalPages = 1
  pageSize = 10
  isLoading = false

  isDetailModalOpen:boolean = false;

  constructor(
    private api:Api,
    private toastr:ToastrService,
    private timeUtil:TimeUtilService
  ){}

  ngOnInit(): void {
    this.loadConsignors();
  }

  loadConsignors(page:number = 1){
    this.isLoading = true;
    this.api.invoke(getPageOfConsignors,{
      page: page - 1,
      size: this.pageSize
    }).then((res:PageResponseConsignorProfileView) =>{
        if(res.content){
          this.consignorRaw = res.content;
          
          this.consignors = res.content.map(c => this.mapToConsignorListItem(c));

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
      this.api.invoke(approveConsignorProfile,{
        'public-id': publicId
      }).then(()=>{
        this.toastr.success("Successfully update status");
        this.closeDetailModal();
        this.loadConsignors(this.currentPage);
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
      if(!this.selectedConsignor?.remark){
        this.toastr.error("Remark is mandatory");
        return;
      }
      this.api.invoke(rejectConsignorProfile,{
        'public-id':publicId,
         body:{text: this.selectedConsignor?.remark}
      }).then(()=>{
        this.toastr.success("Successfully update status");
        this.loadConsignors(this.currentPage);
      }).catch(err => {
        if(err instanceof HttpErrorResponse){
          this.toastr.error(err.error.message);
        }
      })
    }
  }

  activate(publicId:any){
    if(publicId){
      this.api.invoke(activateConsignorProfile,{
        'public-id':publicId
      }).then(()=>{
        this.toastr.success("Successfully update status");
        this.loadConsignors(this.currentPage);
      }).catch(err => {
        if(err instanceof HttpErrorResponse){
          this.toastr.error(err.error.message);
        }
      })
    }
  }

  // 
  suspend(publicId:any){
    if(publicId){
      if(!this.selectedConsignor?.remark){
        this.toastr.error("Remark is mandatory");
        return;
      }
      this.api.invoke(suspendConsignorProfile,{
        'public-id':publicId,
         body:{text: this.selectedConsignor?.remark}
      }).then(()=>{
        this.toastr.success("Successfully update status");
        this.loadConsignors(this.currentPage);
      }).catch(err => {
        if(err instanceof HttpErrorResponse){
          this.toastr.error(err.error.message);
        }
      })
    }
  }

  // close detail modal
  closeDetailModal() {
    this.selectedConsignor = undefined
    this.isDetailModalOpen = false
  }

  // handle update
  handleSave(){
    if(this.selectedConsignor?.remark){
      this.api.invoke(addUserRemark,{
        'public-id': this.selectedConsignor.publicId as string,
        body:{text: this.selectedConsignor?.remark}
      }).then(()=>{
        this.closeDetailModal();
      }).catch(err =>{
        if(err instanceof HttpErrorResponse){
          this.toastr.error(err.error.message);
        }
      })
    }
  }

  // modal methods
  openDetailModal(id: string) {
    this.selectedConsignor = this.consignorRaw.find(a => a.publicId === id);
    if(this.selectedConsignor){
        const createdAt = this.selectedConsignor.createdAt as string;
        const updatedAt = this.selectedConsignor.updatedAt as string;
        this.selectedConsignor.createdAt = this.timeUtil.formatWithMonthName(createdAt)
        this.selectedConsignor.updatedAt = this.timeUtil.formatWithMonthName(updatedAt);
    }
    this.isDetailModalOpen = true;
  }
 

  // helper method
  mapToConsignorListItem(consignor: ConsignorProfileView):ConsignorListItem{
    return {
        id: consignor.publicId,
        status: consignor.status,
        fullName:consignor.firstName + ' ' + consignor.lastName,
        phone: consignor.phone,
        tradeLicence: consignor.tradeLicence,
        businessName: consignor.businessName,
    }

  }

  getBadgeColor(status: string) {
    switch (status) {
      case 'APPROVED': return 'success'
      case 'REJECTED': return 'error'
      default: return 'warning'
    }
  }

  // pagination methods
  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return
    this.loadConsignors(page)
  }
}
