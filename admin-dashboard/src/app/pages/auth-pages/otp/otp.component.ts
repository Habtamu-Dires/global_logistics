import { Component } from '@angular/core';
import { OtpFormComponent } from '../../../shared/components/auth/otp-form/otp-form.component';
import { AuthPageLayoutComponent } from '../../../shared/layout/auth-page-layout/auth-page-layout.component';

@Component({
  selector: 'app-otp',
  imports: [
    AuthPageLayoutComponent,
    OtpFormComponent
  ],
  templateUrl: './otp.component.html',
  styles: ``,
})
export class OtpComponent {

}
