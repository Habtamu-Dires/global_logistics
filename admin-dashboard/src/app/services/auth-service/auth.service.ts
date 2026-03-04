import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Api } from '../api';
import {jwtDecode} from 'jwt-decode';
import { getUserProfile, logout, refresh, sendOtp, verify } from '../functions';
import { UserProfile, VerifyOtpRequest } from '../models';
import { ToastrService } from 'ngx-toastr';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private _token: string | null = null;
  private _refreshToken: string | null = null;
  private _profile: UserProfile | undefined;
  private _decodedToken: any | null = null;
  private startTokenRefreshInterval?: ReturnType<typeof setInterval>;

  constructor(
    private http: HttpClient,
    private router: Router,
    private api: Api,
    private toastr: ToastrService
  ) {}

  get token() {
    return this._token;
  }

  get profile(): UserProfile | undefined {
    return this._profile;
  }

  async init() {
    this.setAccessToken(localStorage.getItem('access_token'));
    this._refreshToken = localStorage.getItem('refresh_token');

    if (this._token && this._refreshToken) {
      if (this.isTokenExpired()) {
        const refreshed = await this.refreshToken();
        if (!refreshed) {
          this.logout();
          return;
        }
      }

      await this.loadProfile();

      this.startTokenRefresh();

      // Since admin-only UI, navigate to admin dashboard if authenticated
      this.router.navigate(['/']);
    } else {
      // Explicitly redirect to login if no tokens (unauthenticated)
      this.router.navigate(['/signin']);
    }
  }

  // set access token and decode it
  private setAccessToken(token: string | null) {
    this._token = token;
    this._decodedToken = token ? jwtDecode(token) : null;
  }

  // login 
  async login(accessToken: string, refreshToken: string) {
    this.setAccessToken(accessToken);
    if(this.isAdmin || this.isSuperAdmin) {
      this.handleLoginSuccess({ access_token: accessToken, refresh_token: refreshToken });
    } else {
      this.toastr.error('Access Denied');
      this.logout();
    }
  }
  

  // Send OTP (called from LoginComponent)
  async sendOtp(phone: string) {
    return await this.api.invoke(sendOtp, { body: { phone } });
  }

  // Verify OTP and login (called from LoginComponent)
  // Returns observable for component to subscribe
  async verifyOtp(req:VerifyOtpRequest) {
    return await this.api.invoke(verify, { body: req });
  }

  // Store tokens after successful verifyOtp (call this in LoginComponent after success)
  async handleLoginSuccess(res: { access_token: string; refresh_token: string }) {
    this._refreshToken = res.refresh_token;
    localStorage.setItem('access_token', this._token ?? '');
    localStorage.setItem('refresh_token', this._refreshToken);
    await this.loadProfile();
    this.startTokenRefresh();
    this.router.navigate(['/']);
  }

  async logout() {
    const refreshToken = this._refreshToken;

    // 1️⃣ Clear UI state immediately
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    this._token = null;
    this._refreshToken = null;
    this._profile = undefined;
    this.stopTokenRefresh();
    this.router.navigate(['/signin']);

    // 2️⃣ Try to revoke in background (do not block logout)
    if (refreshToken) {
      this.api.invoke(logout, { body: { refreshToken } })
        .catch(err => console.error('Logout request failed:', err));
    }
  }

  

  // Is authenticated
  get isAuthenticated(): boolean {
    return !!this._token && !this.isTokenExpired();
  }

  // Check if token is expired (with optional offset in seconds)
  private isTokenExpired(offset: number = 0): boolean {
    if (!this._decodedToken) return true;
    return this._decodedToken.exp < (Date.now() / 1000 + offset);
  }

  // Get roles
  get roles(): string[]  {
    return this._decodedToken?.roles || [];
  }

  // Check if ADMIN
  get isAdmin(): boolean {
    return this.roles.includes('ADMIN');
  }

  // Check if SUPER_ADMIN
  get isSuperAdmin(): boolean {
    return this.roles.includes('SUPER_ADMIN');
  }

  // Get the current access token
  getToken(): string | null {
    return this._token;
  }

  // Load user profile from backend
  private async loadProfile() {
    if (this._profile) return;
    try {
      const profile:UserProfile = await this.api.invoke(getUserProfile);
      this._profile = profile;
    } catch (err:any) {
      if(err instanceof HttpErrorResponse)  {
        try {
          const error = JSON.parse(err.error);
          this.toastr.error(error.message);
        } catch {
          this.toastr.error('Failed to load user profile');
        }
      }
      this.logout();
    }
  }

  // Start proactive token refresh
   startTokenRefresh() {
    if (this.startTokenRefreshInterval) {
      clearInterval(this.startTokenRefreshInterval);
    }
    this.startTokenRefreshInterval = setInterval(async () => {
      const minValidity = 120; // Buffer in seconds
      if (this.isTokenExpired(minValidity)) { 
        try {
          await this.refreshToken();
        } catch (error) {
          console.error('Proactive token refresh failed:', error);
          // Don't logout here; let reactive handle 401
        }
      }
    }, 60000); // Check every 60 seconds
  }

  stopTokenRefresh() {
    if (this.startTokenRefreshInterval) {
      clearInterval(this.startTokenRefreshInterval);
    }
  }

  // Reactive/proactive token refresh
  async refreshToken(minValidity: number = -1): Promise<boolean> {
    if (!this._refreshToken) return false;
    try {
      const res = await this.api.invoke(refresh, { body: { refreshToken: this._refreshToken } })
      .catch(err => {
        console.error('Token refresh failed:', err);
        throw err;
      });
      
      this.setAccessToken(res.accessToken ?? '');
      if (res.refreshToken) {
        this._refreshToken = res.refreshToken;
        localStorage.setItem('refresh_token', this._refreshToken);
      }
      localStorage.setItem('access_token', this._token ?? '');
      return true;
    } catch (error) {
      console.error('Token refresh failed:', error);
      return false;
    }
  }
}