import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-shipment-workspace',
  imports: [],
  templateUrl: './shipment-workspace.component.html',
  styleUrl: './shipment-workspace.component.css',
})
export class ShipmentWorkspaceComponent implements OnInit{


  constructor(
    private activatedRoute:ActivatedRoute
  ){

  }

  ngOnInit(): void {
    const shipmentId = this.activatedRoute.snapshot.params['shipmentId'];
    console.log(shipmentId);
  }

}
