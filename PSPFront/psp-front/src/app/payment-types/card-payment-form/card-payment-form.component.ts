import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-card-payment-form',
  templateUrl: './card-payment-form.component.html',
  styleUrls: ['./card-payment-form.component.css']
})
export class CardPaymentFormComponent implements OnInit {
  paymentForm!: FormGroup;
  paymentId!: string;
  amount!: number;
  currency!: string;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {
    this.paymentForm = this.fb.group({
      pan: ['', [Validators.required, Validators.pattern(/^\d{13,19}$/)]],
      cardHolderName: ['', [Validators.required, Validators.minLength(3)]],
      expiryDate: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/\d{2}$/)]],
      securityCode: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
    });
  }

  ngOnInit(): void {
    this.paymentId = this.route.snapshot.paramMap.get('paymentId') || '';
    
    // Uzmi amount i currency iz query params (ako postoje)
    const amountParam = this.route.snapshot.queryParamMap.get('amount');
    const currencyParam = this.route.snapshot.queryParamMap.get('currency');
    
    this.amount = amountParam ? parseFloat(amountParam) : 0;
    this.currency = currencyParam || 'EUR';

    if (!this.paymentId) {
      this.snackBar.open('Invalid payment ID', 'Close', { duration: 3000 });
      this.router.navigate(['/payment']);
    }
  }

  formatCardNumber(event: any): void {
    let value = event.target.value.replace(/\s/g, '');
    if (value.length > 0) {
      value = value.match(/.{1,4}/g)?.join(' ') || value;
    }
    this.paymentForm.patchValue({ pan: value }, { emitEvent: false });
  }

  formatExpiryDate(event: any): void {
    let value = event.target.value.replace(/\D/g, '');
    if (value.length >= 2) {
      value = value.substring(0, 2) + '/' + value.substring(2, 4);
    }
    this.paymentForm.patchValue({ expiryDate: value }, { emitEvent: false });
  }

  onSubmit(): void {
    if (this.paymentForm.invalid) {
      this.snackBar.open('Please fill all fields correctly', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;

    // Priprema podataka za slanje
    const formValue = this.paymentForm.value;
    const cardData = {
      paymentId: this.paymentId,
      pan: formValue.pan.replace(/\s/g, ''), // Ukloni razmake
      cardHolderName: formValue.cardHolderName,
      expiryDate: formValue.expiryDate,
      securityCode: formValue.securityCode
    };

    // Poziv bank servisa
    this.http.post<any>('http://localhost:8081/api/v1/bank/process-payment', cardData)
      .subscribe({
        next: (response) => {
          this.loading = false;
          if (response.success && response.redirectUrl) {
            // Uspesno plaćanje - redirect na success URL iz response-a
            window.location.href = response.redirectUrl;
          } else if (response.redirectUrl) {
            // Neuspešno plaćanje - redirect na failed/error URL iz response-a
            window.location.href = response.redirectUrl;
          } else {
            // Fallback ako nema redirectUrl
            const successUrl = this.route.snapshot.queryParamMap.get('successUrl') || 
                             'http://localhost:4200/payment-success';
            const failedUrl = this.route.snapshot.queryParamMap.get('failedUrl') || 
                            'http://localhost:4200/payment-failed';
            if (response.success) {
              window.location.href = successUrl + '?transactionId=' + (response.globalTransactionId || '');
            } else {
              window.location.href = failedUrl + '?error=' + encodeURIComponent(response.errorMessage || 'Payment failed');
            }
          }
        },
        error: (error) => {
          this.loading = false;
          console.error('Payment error:', error);
          const errorMessage = error.error?.errorMessage || error.message || 'Payment processing failed';
          this.snackBar.open(errorMessage, 'Close', { duration: 5000 });
          
          // Proveri da li postoji redirectUrl u error response-u
          if (error.error?.redirectUrl) {
            window.location.href = error.error.redirectUrl;
          } else {
            // Fallback - redirect na error URL
            const errorUrl = this.route.snapshot.queryParamMap.get('errorUrl') || 
                            'http://localhost:4200/payment-error';
            setTimeout(() => {
              window.location.href = errorUrl + '?error=' + encodeURIComponent(errorMessage);
            }, 2000);
          }
        }
      });
  }

  get pan() { return this.paymentForm.get('pan'); }
  get cardHolderName() { return this.paymentForm.get('cardHolderName'); }
  get expiryDate() { return this.paymentForm.get('expiryDate'); }
  get securityCode() { return this.paymentForm.get('securityCode'); }
}
