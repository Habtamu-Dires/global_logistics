import { Component, OnInit } from '@angular/core';
import { InputFieldComponent } from './../../form/input/input-field.component';
import { ModalService } from '../../../services/modal.service';

import { ModalComponent } from '../../ui/modal/modal.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { UserProfile } from '../../../../services/models';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { Api } from '../../../../services/api';
import { updateUserProfile } from '../../../../services/functions';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-user-meta-card',
  imports: [
    ModalComponent,
    InputFieldComponent,
    ButtonComponent
],
  templateUrl: './user-meta-card.component.html',
  styles: ``
})
export class UserMetaCardComponent  {

  user: UserProfile;

  isOpen = false;

  constructor(
    public modal: ModalService,
    private authService: AuthService,
    private api:Api,
    private toastr:ToastrService
  ) {
    this.user = this.authService.profile as UserProfile;
  }

  
  openModal() { this.isOpen = true; }
  closeModal() { this.isOpen = false; }  

  handleSave() {
    this.updateProfile();
    this.modal.closeModal();
  }

  updateProfile(){
    this.api.invoke(updateUserProfile,{body:this.user}).then(res=>{
      this.user = res as UserProfile;
      this.toastr.success('Profile updated successfully');
      this.closeModal();
    }).catch(err=>{
      if(err instanceof HttpErrorResponse){
        const error = JSON.parse(err.error);
        this.toastr.error(error.message);
      } else {
        console.log(err);
        this.toastr.error('An unexpected error occurred');
      }
    })
  }

}
