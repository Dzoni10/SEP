import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';

export interface CartItem {
  carId: number;
  price: number;
  rentalDays: number;
}

export interface CheckoutRequest {
  items: CartItem[];
  userId: number;
  paymentMethod: string;
}

export interface CheckoutResponse {
  status: string;
  orderId: number;
  redirectUrl: string;
  transactionId: string;
  totalAmount: number;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api/v1/orders';

  constructor(private http: HttpClient, private auth: AuthService) {}

  checkout(request: CheckoutRequest): Observable<CheckoutResponse> {
    return this.http.post<CheckoutResponse>(this.apiUrl + '/checkout', request, {
      headers: this.auth.getAuthHeaders()
    });
  }
}
