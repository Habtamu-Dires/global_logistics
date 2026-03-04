import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ButtonComponent } from "../../ui/button/button.component";
import { BadgeComponent } from "../../ui/badge/badge.component";
import { Api } from '../../../../services/api';
import { getPageOfShipmentsByStage } from '../../../../services/functions';
import { PageResponseShipmentSummary, ShipmentSummary } from '../../../../services/models';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { TimeUtilService } from '../../../../services/time-util/time-util.service';
import { Router } from '@angular/router';
import { pushAll } from '@amcharts/amcharts5/.internal/core/util/Array';

export type ShipmentStage = 'NEW' | 'AGREED' | 'EXECUTION' | 'COMPLETED' | 'CANCELLED';

@Component({
  selector: 'app-shipment-table',
  imports: [CommonModule, ButtonComponent, BadgeComponent],
  templateUrl: './shipment-table.component.html',
  styles: ``,
})
export class ShipmentsComponent implements OnInit{

  stages:ShipmentStage[] = ['NEW', 'AGREED', 'EXECUTION', 'COMPLETED', 'CANCELLED'];
  selectedStage:ShipmentStage = 'NEW';

  shipments:ShipmentSummary[] = [];

  currentPage = 1
  totalPages = 1
  pageSize = 10
  isLoading = false

  constructor(
    private api:Api,
    private toastr:ToastrService,
    public timeUtil:TimeUtilService,
    private router:Router
  ){}

  ngOnInit(): void {
    this.loadShipmentsByStage();
  }

  loadShipmentsByStage( page:number=1){
    this.api.invoke(getPageOfShipmentsByStage,{
      'stage':this.selectedStage,
      page: page - 1,
      size: this.pageSize
    }).then((res:PageResponseShipmentSummary)=>{
        console.log(res.content);
        if(res.content){
          this.shipments = res.content;

          this.currentPage = Number(res.number ?? 0) + 1
          this.totalPages  = res.totalPages ?? 1
        }
    }).catch(err => {
      console.log(err);
      if(err instanceof HttpErrorResponse){
        this.toastr.error(err.error.message);
      }
    })
  }

  changeStage(stage:string){
    this.selectedStage = stage as ShipmentStage;
    this.loadShipmentsByStage();
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
  }

  // goto 
  goToShipmentWorkspace(shipmentId:any){
    if(shipmentId){
      this.router.navigate(['shipment-workspace',shipmentId]);
    }
  }
  
}
