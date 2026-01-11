import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PaymentComponent } from './payment-types/payment/payment.component';
import { CardPaymentFormComponent } from './payment-types/card-payment-form/card-payment-form.component';

const routes: Routes = [
  {path:'payment', component:PaymentComponent},
  {path:'payment/card/:paymentId', component:CardPaymentFormComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
