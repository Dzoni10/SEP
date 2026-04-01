import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WebShopComponent } from './webshop/webshsop.component';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule } from '@angular/material/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ImageDialogComponent } from './image-dialog/image-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { SuccessPaymentComponent } from './success-payment/success-payment.component';
import { FailedPaymentComponent } from './failed-payment/failed-payment.component';
import { ErrorPaymentComponent } from './error-payment/error-payment.component';
import { AppRoutingModule } from "src/app/app-routing.module";



@NgModule({
  declarations: [
    WebShopComponent,
    ImageDialogComponent,
    SuccessPaymentComponent,
    FailedPaymentComponent,
    ErrorPaymentComponent
  ],
  imports: [
    CommonModule,
    MatCardModule,
    MatFormFieldModule,
    MatOptionModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatDialogModule,
    AppRoutingModule,
    FormsModule
]
})
export class ShopModule { }
