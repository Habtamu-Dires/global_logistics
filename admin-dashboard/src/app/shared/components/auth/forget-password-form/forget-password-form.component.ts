import { Component } from '@angular/core';
import { InputFieldComponent } from "../../form/input/input-field.component";
import { LabelComponent } from "../../form/label/label.component";
import { ButtonComponent } from "../../ui/button/button.component";
import { Router } from "@angular/router";
import { Api } from '../../../../services/api';
import { forgetPassword } from '../../../../services/functions';
import { ToastrService } from 'ngx-toastr';
import { LoginService } from '../../../services/login.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-forget-password-form',
  imports: [InputFieldComponent, LabelComponent, ButtonComponent],
  templateUrl: './forget-password-form.component.html',
  styles: ``,
})
export class ForgetPasswordFormComponent {

  phone: string = '';

  constructor(
    private router:Router,
    private api:Api,
    private toastr: ToastrService,
    private loginService: LoginService
  ){}

  async onSendOtp() {
    // Implement OTP sending logic here
    if(this.phone.length === 10){ 
       await this.api.invoke(forgetPassword, { body: { phone: this.phone } }).then(() => {
        this.loginService.setPhone(this.phone);
        this.toastr.success('OTP sent successfully');
        this.router.navigate(['reset-password']);
      }).catch((err:HttpErrorResponse) => {
        const error = JSON.parse(err.error);
        this.toastr.error(error.message);
      });
    } else {
      this.toastr.error('Please enter your phone number');
    }

  }

  backToLogin(){
    this.router.navigate(['signin']);
  }
}
