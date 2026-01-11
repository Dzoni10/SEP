import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentComponent } from './payment/payment.component';
import {MatCardModule} from "@angular/material/card"
import { MatSnackBarModule } from '@angular/material/snack-bar';
import {MatIconModule} from '@angular/material/icon'

@NgModule({
  declarations: [
    PaymentComponent
  ],
  imports: [
    CommonModule,
    MatCardModule,
    MatSnackBarModule,
    MatIconModule
  ]
})
export class PaymentTypesModule { }
