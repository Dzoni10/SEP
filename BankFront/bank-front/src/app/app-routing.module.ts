import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CardPaymentComponent } from './transaction/card-payment/card-payment.component';
import { PaypalPaymentComponent } from './transaction/paypal-payment/paypal-payment.component';
import { QrPaymentComponent } from './transaction/qr-payment/qr-payment.component';
import { PaymentPageComponent } from './transaction/payment-page/payment-page.component';

const routes: Routes = [
  { path: 'pay/:paymentId', component: PaymentPageComponent },
  {path:'card', component:CardPaymentComponent},
  {path:'paypal',component:PaypalPaymentComponent},
  {path:'qr', component:QrPaymentComponent}
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
