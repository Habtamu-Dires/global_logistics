import { Component, ElementRef, QueryList, ViewChildren, OnInit, Input } from '@angular/core';
import { LabelComponent } from '../../form/label/label.component';
import { CheckboxComponent } from '../../form/input/checkbox.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { InputFieldComponent } from '../../form/input/input-field.component';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../../services/login.service';
import { Api } from '../../../../services/api';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthTokens } from '../../../../services/models';


@Component({
  selector: 'app-otp-form',
  imports: [
    LabelComponent,
    ButtonComponent,
    RouterModule,
    FormsModule
  ],
  templateUrl: './otp-form.component.html',
  styles: ``,
})
export class OtpFormComponent {

  @ViewChildren('otpInput') inputs!: QueryList<ElementRef>;
  
  rawPhone = ""; 
  maskedPhone = "";

  constructor(
    private loginService: LoginService,
    private router: Router,
    private authService: AuthService,
    private toastr: ToastrService
  ){}

  ngOnInit() {
    this.rawPhone = this.loginService.currentPhone;
    if(this.rawPhone === "") {
      this.router.navigate(['/signin']);
    } 
    this.formatPhone();
  }

  onVerify() {
    const code = this.inputs.map(i => i.nativeElement.value).join('');
    if(code.length < 6) {
      this.toastr.error('Please enter the 6-digit OTP', 'Invalid OTP');
      return;
    }
    this.authService.verifyOtp({ phone: this.rawPhone, code }).then((res:AuthTokens) => {
      if(res.accessToken && res.refreshToken) {
        this.authService.login(res.accessToken, res.refreshToken);
      } else {
        this.toastr.error('Invalid response from server', 'Verification Failed');
      }
    }).catch((err) => {
        if(err instanceof HttpErrorResponse) {
          const error = JSON.parse(err.error);
          this.toastr.error(error.message, 'Verification Failed');
        } else {
          console.error('An unexpected error occurred:', err);
          this.toastr.error('Failed to verify OTP', 'Error');
        }
      });
    }
    
   onResend() {
    this.authService.sendOtp(this.rawPhone).then(() => {
      this.toastr.success('OTP has been resent to your phone', 'OTP Sent');
    }).catch((err) => {
      if (err instanceof HttpErrorResponse) {
        const error = JSON.parse(err.error);
        
        if(error?.code === 'OTP_ALREADY_VERIFIED'){ 
          this.toastr.info('Phone is already verified. Please login.', 'Already Verified');
          this.router.navigate(['/signin']);
        } else {
          this.toastr.error(error?.message);
        }
      } else {
        console.error('An unexpected error occurred:', err);
        this.toastr.error('Failed to resend OTP', 'Error');
      }
    });
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
