import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { SignupComponent } from './auth/signup/signup.component';
import { WebShopComponent } from './shop/webshop/webshsop.component';
import { AdminPanelComponent } from './auth/admin-panel/admin-panel.component';
import { SuccessPaymentComponent } from './shop/success-payment/success-payment.component';
import { FailedPaymentComponent } from './shop/failed-payment/failed-payment.component';
import { ErrorPaymentComponent } from './shop/error-payment/error-payment.component';
import { PaymentGuard } from './auth/guard/payment.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent},
  { path: 'webshop', component: WebShopComponent},
  { path: 'panel', component: AdminPanelComponent},
  { path: 'payment-success', component: SuccessPaymentComponent, canActivate:[PaymentGuard]},
  { path: 'payment-failed', component: FailedPaymentComponent, canActivate:[PaymentGuard]},
  { path: 'payment-error', component: ErrorPaymentComponent, canActivate:[PaymentGuard]},
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
