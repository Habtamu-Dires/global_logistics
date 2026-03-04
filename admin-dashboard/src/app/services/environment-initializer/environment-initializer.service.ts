import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.development';

declare var window: any;

@Injectable({
  providedIn: 'root'
})
export class EnvironmentInitializerService {

  constructor() { }

  async initialize(){
    if(window.API_URL && window.API_URL !== '__API_URL_PLACEHOLDER__'){
      environment.apiUrl = window.API_URL || 'https:global-logistics.com/api';
    } else {
      environment.apiUrl = 'http://localhost:8088/api';
    }

  }
}
