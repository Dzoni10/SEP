import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BankTransaction } from './model/BankTransaction';
import { PaymentSubmitRequest } from './model/PaymentSubmitRequest';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  
  private apiUrl = 'https://localhost:8082/api/v1/bank/payment';

  constructor(private http: HttpClient) { }

  getPaymentDetails(paymentId: string): Observable<BankTransaction> {
    return this.http.get<BankTransaction>(`${this.apiUrl}/${paymentId}`);
  }

  submitCardPayment(paymentId: string, request: PaymentSubmitRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/${paymentId}/submit`, request, { responseType: 'text' });
  }

  getQrData(paymentId: string): Observable<{qrString:string, base64Image: string}> {
    return this.http.get<{qrString: string, base64Image: string}>(`${this.apiUrl}/${paymentId}/qr-data`);
  }

  submitQrPayment(paymentId: string, scannedString: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/${paymentId}/qr-submit`,scannedString, { responseType: 'text' });
  }

}
