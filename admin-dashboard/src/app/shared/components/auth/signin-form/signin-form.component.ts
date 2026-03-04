
import { Component } from '@angular/core';
import { LabelComponent } from '../../form/label/label.component';
import { CheckboxComponent } from '../../form/input/checkbox.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { InputFieldComponent } from '../../form/input/input-field.component';
import { Route, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Api } from '../../../../services/api';
import { login } from '../../../../services/functions';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { LoginService } from '../../../services/login.service';
import { AuthTokens } from '../../../../services/models';

@Component({
  selector: 'app-signin-form',
  imports: [
    LabelComponent,
    CheckboxComponent,
    ButtonComponent,
    InputFieldComponent,
    RouterModule,
    FormsModule
],
  templateUrl: './signin-form.component.html',
  styles: ``
})
export class SigninFormComponent {

  showPassword = false;
  isChecked = false;

  phone = '';
  password = '';

  constructor(
    private api:Api,
    private router: Router,
    private toastr: ToastrService,
    private authService: AuthService,
    private loginService: LoginService
  ) {}

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSignIn() {
    if(this.phone && this.password) {
      this.login({ phone: this.phone, password: this.password });
    }

  }

  // login
  async login(AuthenticationRequest: { phone: string; password: string }) {
      try {
        const res:AuthTokens = await this.api.invoke(login, { body: AuthenticationRequest });
        if(res.isTempPassword){
          this.loginService.setPhone(AuthenticationRequest.phone);
          this.router.navigate(['/change-temp-password']);
          return;
        }

        if(res.accessToken && res.refreshToken) {
          await this.authService.login(res.accessToken, res.refreshToken);
        } else {
          this.toastr.error('Invalid response from server', 'Login Failed');
        }
      } catch (err: any) {
        if (err instanceof HttpErrorResponse) {
          
          console.log('Status:', err.status);
          console.log('Error message:', err.error?.message);
          console.log('Error code:', err.error?.code);
          
          if(err?.error.code === 'ERR_NOT_VERIFIED'){ 
            this.toastr.info(err.error?.message, 'Verification Required');
            this.loginService.setPhone(AuthenticationRequest.phone);
            this.router.navigate(['/otp']);
          } else {
            this.toastr.error(err.error?.message, 'Login Failed');
          }

        } else {
          console.error('An unexpected error occurred:', err);
          this.toastr.error('Login Failed');
        }
       }
     }
 }

