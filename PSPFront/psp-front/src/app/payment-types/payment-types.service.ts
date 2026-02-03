import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InitiatePaymentRequest {
  orderId: number;
  amount: number;
  currency: string;
  paymentMethod: string;
  callbackUrl: string;
  metadata: { [key: string]: string };
}

export interface InitiatePaymentResponse {
  success: boolean;
  redirectUrl: string;
  transactionId: string;
  errorMessage?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PaymentTypesService {
  private pspApiUrl = 'http://localhost:8081/api/v1/psp';

  constructor(private http: HttpClient) {}

  initiatePayment(webShopId: number, request: InitiatePaymentRequest): Observable<InitiatePaymentResponse> {
    return this.http.post<InitiatePaymentResponse>(
      `${this.pspApiUrl}/webshop/${webShopId}/pay`,
      request
    );
  }
}
