import { Component, OnInit } from '@angular/core';
import { ButtonComponent } from "../../ui/button/button.component";
import { InputFieldComponent } from "../../form/input/input-field.component";
import { LabelComponent } from "../../form/label/label.component";
import { Api } from '../../../../services/api';
import { LoginService } from '../../../services/login.service';
import { changePassword } from '../../../../services/functions';
import { ChangePasswordRequest } from '../../../../services/models';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../../services/auth-service/auth.service';

@Component({
  selector: 'app-change-password-form',
  imports: [ButtonComponent, InputFieldComponent, LabelComponent],
  templateUrl: './change-password-form.component.html',
  styles: ``,
})
export class ChangePasswordFormComponent implements OnInit{
  
  phone: string | undefined;
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  showPassword = false;
  isAuthenticated = false;

  constructor(
    private api: Api,
    private loginService: LoginService,
    private toastr: ToastrService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.phone = this.loginService.currentPhone;
    this.isAuthenticated = this.authService.isAuthenticated;
    if(this.isAuthenticated){
      this.phone = this.authService.profile?.phone;
    }
  }

  // Logic to check if new passwords match
  get isMismatch(): boolean {
    return this.confirmPassword.length > 0 && this.newPassword !== this.confirmPassword;
  }

  // Logic to ensure the form is ready for submission
  get isFormValid(): boolean {
    const hasCurrent = this.currentPassword.length > 0;
    const hasNew = this.newPassword.length >= 4;
    const matches = !this.isMismatch;
    return hasCurrent && hasNew && matches;
  }

   onChangePassword() {  
    if (this.isFormValid && this.phone) {
      const payload:ChangePasswordRequest = {
        phone: this.phone,
        currentPassword: this.currentPassword,
        newPassword: this.newPassword,
        confirmPassword: this.confirmPassword
      };
      this.api.invoke(changePassword, { body: payload }).then(() => {
         this.toastr.success('Password changed successfully');
         if(this.isAuthenticated){
          this.router.navigate(['/']);
         }else{
          this.router.navigate(['/signin']);
         }
      }).catch((err) => {
         if(err instanceof HttpErrorResponse)  {
            const error = JSON.parse(err.error);
            this.toastr.error(error.message);
         }  else {
           this.toastr.error('Failed to change password');
         }   
      });
    } else{
      this.toastr.error('Please fill out the form correctly');
    }
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }
}
