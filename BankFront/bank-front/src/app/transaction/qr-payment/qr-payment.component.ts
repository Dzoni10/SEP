import { Component, Input, OnInit } from '@angular/core';
import { BankTransaction } from '../model/BankTransaction';
import { TransactionService } from '../transaction.service';

@Component({
  selector: 'app-qr-payment',
  templateUrl: './qr-payment.component.html',
  styleUrls: ['./qr-payment.component.css']
})
export class QrPaymentComponent implements OnInit{
  @Input() paymentId!: string;

  transactionData: BankTransaction | null = null;
  qrData: {qrString:string, base64Image: string} | null = null;
  isProcessing = false;

  constructor(private transactionService: TransactionService){}

  ngOnInit(): void {
    this.transactionService.getPaymentDetails(this.paymentId).subscribe(data => this.transactionData = data);
    this.transactionService.getQrData(this.paymentId).subscribe(data => this.qrData = data);
  }

  simulateScan(): void {
    if (!this.qrData) return;
    this.isProcessing = true;

    this.transactionService.submitQrPayment(this.paymentId, this.qrData.qrString).subscribe({
      next: (redirectUrl) => {
        const cleanUrl = redirectUrl.replace(/"/g,'');
        window.location.href = cleanUrl;
      },
      error: () => this.isProcessing = false
    });
  }


}
