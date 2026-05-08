import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardPaymentComponent } from './card-payment/card-payment.component';
import { QrPaymentComponent } from './qr-payment/qr-payment.component';
import { PaypalPaymentComponent } from './paypal-payment/paypal-payment.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {MatSnackBarModule} from '@angular/material/snack-bar'
import {MatCardModule} from '@angular/material/card'
import { MatButtonModule } from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatSelectModule} from '@angular/material/select'
import {MatInputModule} from '@angular/material/input'
import {MatIconModule} from '@angular/material/icon'
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner'
import { QRCodeModule } from 'angularx-qrcode';
import { PaymentPageComponent } from './payment-page/payment-page.component';

@NgModule({
  declarations: [
    CardPaymentComponent,
    QrPaymentComponent,
    PaypalPaymentComponent,
    PaymentPageComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatSnackBarModule,
    MatCardModule,
    MatButtonModule,
    MatSelectModule,
    MatInputModule,
    MatIconModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    QRCodeModule
  ]
})
export class TransactionModule {
  
 } 
