import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PaymentTypesService } from '../payment-types.service';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent implements OnInit {

  orderId: number | null = null;
  webShopId: number | null = null;
  amount: number | null = null;
  currency: string = 'EUR';
  callbackUrl: string | null = null;
  successUrl: string | null = null;
  failedUrl: string | null = null;
  errorUrl: string | null = null;
  userId: number | null = null;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private paymentService: PaymentTypesService
  ) {}

  ngOnInit(): void {
    const params = this.route.snapshot.queryParamMap;
    this.orderId = params.get('orderId') ? +params.get('orderId')! : null;
    this.webShopId = params.get('webShopId') ? +params.get('webShopId')! : null;
    this.amount = params.get('amount') ? +params.get('amount')! : null;
    this.currency = params.get('currency') || 'EUR';
    this.callbackUrl = params.get('callbackUrl');
    this.successUrl = params.get('successUrl');
    this.failedUrl = params.get('failedUrl');
    this.errorUrl = params.get('errorUrl');
    this.userId = params.get('userId') ? +params.get('userId')! : null;
  }

  selectPayment(method: string) {
    if (method === 'card') {
      this.initiateCardPayment();
    } else {
      this.snackBar.open(`Payment method ${method.toUpperCase()} will be available soon`, 'Close', {
        duration: 4000,
        horizontalPosition: 'center'
      });
    }
  }

  private initiateCardPayment() {
    if (!this.orderId || !this.webShopId || !this.amount || !this.callbackUrl) {
      this.snackBar.open('Missing payment data. Please start from the web shop.', 'Close', {
        duration: 4000,
        horizontalPosition: 'center'
      });
      return;
    }

    this.loading = true;
    this.paymentService.initiatePayment(this.webShopId, {
      orderId: this.orderId,
      amount: this.amount,
      currency: this.currency,
      paymentMethod: 'CARD',
      callbackUrl: this.callbackUrl,
      metadata: {
        orderId: String(this.orderId),
        userId: this.userId ? String(this.userId) : '',
        successUrl: this.successUrl || '',
        failedUrl: this.failedUrl || '',
        errorUrl: this.errorUrl || ''
      }
    }).subscribe({
      next: (response) => {
        this.loading = false;
        if (response.success && response.redirectUrl) {
          window.location.href = response.redirectUrl;
        } else {
          this.snackBar.open(response.errorMessage || 'Payment initiation failed', 'Close', {
            duration: 4000,
            horizontalPosition: 'center'
          });
        }
      },
      error: (err) => {
        this.loading = false;
        this.snackBar.open(err.error?.message || err.message || 'Payment initiation failed', 'Close', {
          duration: 5000,
          horizontalPosition: 'center'
        });
      }
    });
  }
}
