import { Routes } from '@angular/router';
import { EcommerceComponent } from './pages/dashboard/ecommerce/ecommerce.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { FormElementsComponent } from './pages/forms/form-elements/form-elements.component';
import { BasicTablesComponent } from './pages/tables/basic-tables/basic-tables.component';
import { BlankComponent } from './pages/blank/blank.component';
import { NotFoundComponent } from './pages/other-page/not-found/not-found.component';
import { AppLayoutComponent } from './shared/layout/app-layout/app-layout.component';
import { InvoicesComponent } from './pages/invoices/invoices.component';
import { LineChartComponent } from './pages/charts/line-chart/line-chart.component';
import { BarChartComponent } from './pages/charts/bar-chart/bar-chart.component';
import { AlertsComponent } from './pages/ui-elements/alerts/alerts.component';
import { AvatarElementComponent } from './pages/ui-elements/avatar-element/avatar-element.component';
import { BadgesComponent } from './pages/ui-elements/badges/badges.component';
import { ButtonsComponent } from './pages/ui-elements/buttons/buttons.component';
import { ImagesComponent } from './pages/ui-elements/images/images.component';
import { VideosComponent } from './pages/ui-elements/videos/videos.component';
import { SignInComponent } from './pages/auth-pages/sign-in/sign-in.component';
import { SignUpComponent } from './pages/auth-pages/sign-up/sign-up.component';
import { CalenderComponent } from './pages/calender/calender.component';
import { authGuard } from './services/guard/auth.guard';
import { OtpComponent } from './pages/auth-pages/otp/otp.component';
import { ForgetPasswordComponent } from './pages/auth-pages/forget-password/forget-password.component';
import { ResetPasswordComponent } from './pages/auth-pages/reset-password/reset-password.component';
import { AdminsComponent } from './pages/users/admins/admins.component';
import { DriversComponent } from './pages/users/drivers/drivers.component';
import { ConsignorsComponent } from './pages/users/consignors/consignors.component';
import { ChangeTempPasswordComponent } from './pages/auth-pages/change-temp-password/change-temp-password.component';
import { ChangePasswordComponent } from './pages/change-password/change-password.component';
import { RegisteredUsersComponent } from './pages/users/registered-users/registered-users.component';
import { ShipmentsComponent } from './shared/components/shipment/shipment-table/shipment-table.component';
import { ShipmentComponent } from './pages/shipment/shipment.component';
import { ShipmentWorkspaceComponent } from './shared/components/shipment/shipment-workspace/shipment-workspace.component';


export const routes: Routes = [
  {
    path:'',
    component:AppLayoutComponent,
    canActivate:[authGuard],
    children:[
      {
        path: '',
        component: EcommerceComponent,
        pathMatch: 'full',
        title:
          'Angular Ecommerce Dashboard',
      },
      //change password
      {
        path:'change-password',
        component:ChangePasswordComponent,
        title:'Angular Change Password Dashboard'
      },
      //users
      {
        path:'admins',
        component:AdminsComponent,
        title:'Angular Admins Dashboard'
      },
      {
        path:'drivers',
        component:DriversComponent,
        title:'Angular Drivers Dashboard'
      },
      {
        path:'consignors',
        component:ConsignorsComponent,
        title:'Angular Consignors Dashboard'
      },
      {
        path:'registered-users',
        component:RegisteredUsersComponent,
        title:'Registered users '
      },
      // shipments
      {
        path:'shipments',
        component:ShipmentComponent,
        title:'Shipments'
      },
      {
        path:'shipment-workspace/:shipment-id',
        component:ShipmentWorkspaceComponent,
        title:'Shipment Workspace'
      },
      {
        path:'calendar',
        component:CalenderComponent,
        title:'Angular Calender'
      },
      {
        path:'profile',
        component:ProfileComponent,
        title:'Angular Profile Dashboard'
      },
      {
        path:'form-elements',
        component:FormElementsComponent,
        title:'Angular Form Elements Dashboard'
      },
      {
        path:'basic-tables',
        component:BasicTablesComponent,
        title:'Angular Basic Tables Dashboard'
      },
      {
        path:'blank',
        component:BlankComponent,
        title:'Angular Blank Dashboard'
      },
      // support tickets
      {
        path:'invoice',
        component:InvoicesComponent,
        title:'Angular Invoice Details Dashboard'
      },
      {
        path:'line-chart',
        component:LineChartComponent,
        title:'Angular Line Chart Dashboard'
      },
      {
        path:'bar-chart',
        component:BarChartComponent,
        title:'Angular Bar Chart Dashboard'
      },
      {
        path:'alerts',
        component:AlertsComponent,
        title:'Angular Alerts Dashboard'
      },
      {
        path:'avatars',
        component:AvatarElementComponent,
        title:'Angular Avatars Dashboard'
      },
      {
        path:'badge',
        component:BadgesComponent,
        title:'Angular Badges Dashboard'
      },
      {
        path:'buttons',
        component:ButtonsComponent,
        title:'Angular Buttons Dashboard'
      },
      {
        path:'images',
        component:ImagesComponent,
        title:'Angular Images Dashboard'
      },
      {
        path:'videos',
        component:VideosComponent,
        title:'Angular Videos Dashboard'
      },
    ]
  },
  // auth pages
  {
    path:'signin',
    component:SignInComponent,
    title:'Angular Sign In Dashboard'
  },
  {
    path:'otp',
    component:OtpComponent,
    title:'Angular OTP Dashboard'
  },
  {
    path:'forget-password',
    component:ForgetPasswordComponent,
    title:'Angular Forget Password Dashboard'
  },{
    path:'reset-password',
    component:ResetPasswordComponent,
    title:'Angular Reset Password Dashboard'
  },
  {
    path:'change-temp-password',
    component:ChangeTempPasswordComponent,
    title:'Angular Change Temporary Password Dashboard'
  },
  // error pages
  {
    path:'**',
    component:NotFoundComponent,
    title:'Angular NotFound Dashboard'
  },
];
