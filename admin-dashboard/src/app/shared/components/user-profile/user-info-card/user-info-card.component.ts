import { Component } from '@angular/core';
import { ModalService } from '../../../services/modal.service';

import { InputFieldComponent } from '../../form/input/input-field.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { LabelComponent } from '../../form/label/label.component';
import { ModalComponent } from '../../ui/modal/modal.component';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { UserProfile } from '../../../../services/models';
import { Api } from '../../../../services/api';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { updateUserProfile } from '../../../../services/functions';

@Component({
  selector: 'app-user-info-card',
  imports: [
    InputFieldComponent,
    ButtonComponent,
    LabelComponent,
    ModalComponent
],
  templateUrl: './user-info-card.component.html',
  styles: ``
})
export class UserInfoCardComponent {

  user:UserProfile;
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
    // Handle save logic here
    console.log('Saving changes...');
    console.log('Updated user data:', this.user);
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
