import { Component, ElementRef, input, OnInit, QueryList, ViewChildren } from '@angular/core';
import { LabelComponent } from "../../form/label/label.component";
import { InputFieldComponent } from "../../form/input/input-field.component";
import { ButtonComponent } from "../../ui/button/button.component";
import { Router } from '@angular/router';
import { LoginService } from '../../../services/login.service';
import { Api } from '../../../../services/api';
import { ToastrService } from 'ngx-toastr';
import { resetPassword } from '../../../../services/functions';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-reset-password-form',
  imports: [LabelComponent, InputFieldComponent, ButtonComponent],
  templateUrl: './reset-password-form.component.html',
  styles: ``,
})
export class ResetPasswordFormComponent implements OnInit {
  
   
  @ViewChildren('otpInput') inputs!: QueryList<ElementRef>;
  
  rawPhone = ""; 
  maskedPhone = "";

  showPassword:boolean = false;
  newPassword: string = '';
  confirmPassword:string = '';

  constructor(
    private router: Router,
    private loginService: LoginService,
    private api: Api,
    private toastr: ToastrService
  ){}

  ngOnInit() {  
    this.rawPhone = this.loginService.currentPhone;
    if(this.rawPhone === "") {
      this.router.navigate(['/signin']);
    } 
    this.formatPhone();
  }

  onResetPassword(){
    if(this.newPassword !== this.confirmPassword) {
      this.toastr.error('Passwords do not match');
      return;
    }

    const otpCode = this.inputs.map(i => i.nativeElement.value).join('');
    if(otpCode.length < 6) {
      this.toastr.error('Please enter the 6-digit OTP', 'Invalid OTP');
      return;
    }
    // Implement password reset logic here
    this.api.invoke(resetPassword, { body: { 
      phone: this.rawPhone, 
      otpCode: otpCode,
      newPassword: this.newPassword , 
      confirmPassword: this.confirmPassword
    } }).then(() => {
      this.toastr.success('Password reset successfully');
      this.router.navigate(['/signin']);
    }).catch((err:HttpErrorResponse) => {
      const error = JSON.parse(err.error);
      if(error.validationErrors && error.validationErrors.length > 0) {
        this.toastr.error(error.validationErrors[0].message);
      } else {
        this.toastr.error(error.message);
      }
    });
  }

  togglePasswordVisibility(){
    this.showPassword = !this.showPassword;
  }

  // 3. Masking logic: 09 **** 12
  formatPhone() {
    if (this.rawPhone.length > 4) {
      const firstTwo = this.rawPhone.substring(0, 2);
      const lastTwo = this.rawPhone.slice(-2);
      this.maskedPhone = `${firstTwo} **** ${lastTwo}`;
    }
  }

  // 1. Auto-focus next box
  onOtpInput(event: any, index: number) {
    const val = event.target.value;
    if (val && index < 5) {
      this.inputs.toArray()[index + 1].nativeElement.focus();
    }
  }

  onOtpDelete(event: any, index: number) {
    if (!event.target.value && index > 0) {
      this.inputs.toArray()[index - 1].nativeElement.focus();
    }
  }

  // 2. Clipboard Paste Logic
  onPaste(event: ClipboardEvent) {
    event.preventDefault();
    const pastedData = event.clipboardData?.getData('text').slice(0, 6).split('');
    if (!pastedData) return;

    const inputElements = this.inputs.toArray();
    pastedData.forEach((char, index) => {
      if (inputElements[index]) {
        inputElements[index].nativeElement.value = char;
      }
    });

    // Focus the last filled box or the next empty one
    const nextIndex = Math.min(pastedData.length, 5);
    inputElements[nextIndex].nativeElement.focus();
  }
}
