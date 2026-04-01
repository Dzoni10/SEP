import { Component, Input, OnInit } from '@angular/core';
import { BankTransaction } from '../model/BankTransaction';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TransactionService } from '../transaction.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-card-payment',
  templateUrl: './card-payment.component.html',
  styleUrls: ['./card-payment.component.css']
})
export class CardPaymentComponent implements OnInit {

  @Input() paymentId!: string;

  transactionData: BankTransaction | null= null;
  paymentForm!: FormGroup;
  isLoading = false;

  constructor(private fb: FormBuilder, private transactionService: TransactionService, private matSnackBar: MatSnackBar){

  this.paymentForm = this.fb.group({
      cardHolderName: ['', Validators.required],
      pan: ['', [Validators.required, Validators.pattern('^[0-9]{13,19}$')]],
      expirationDate: ['', [Validators.required, Validators.pattern('^(0[1-9]|1[0-2])\/[0-9]{2}$')]], // MM/YY format
      securityCode: ['', [Validators.required, Validators.pattern('^[0-9]{3,4}$')]]
    });
  }

  ngOnInit(): void {
    this.transactionService.getPaymentDetails(this.paymentId).subscribe({
      next: (data) => this.transactionData = data,
      error: () => this.matSnackBar.open('Error during transaction load', 'Close')
    });
  }

  onSubmit(): void {
    if (this.paymentForm.invalid) {
      this.paymentForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.transactionService.submitCardPayment(this.paymentId, this.paymentForm.value).subscribe({
      next: (redirectUrl) => {
        const cleanUrl = redirectUrl.replace(/"/g, '');
        window.location.href = cleanUrl;
      },
      error: (err) => {
        this.isLoading = false;
        this.matSnackBar.open(err.error || 'Payment denied.', 'Close', { duration: 4000 });
      }
    });
  }

}
