import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
    
    private phone = new BehaviorSubject<string>('');
    private otpPurpose = new BehaviorSubject<string>(''); // 'login' or 'forget-password'

    /** Observable for phone number */
    phone$: Observable<string> = this.phone.asObservable();

    otpPurpose$: Observable<string> = this.otpPurpose.asObservable();
    
    // set phone number
    setPhone(phone: string): void {
      this.phone.next(phone);
    }

    /** Get current phone number synchronously */
    get currentPhone(): string {
      return this.phone.value;
    }

    // set OTP purpose
    setOtpPurpose(purpose: string): void {
      this.otpPurpose.next(purpose);
    }

    get currentOtpPurpose(): string {
      return this.otpPurpose.value;
    }
}