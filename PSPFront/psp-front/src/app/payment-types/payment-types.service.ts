import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaymentTypesService {

  private apiUrl = 'https://localhost:8081/api/v1/psp'
  private webShopOrderUrl = 'https://localhost:8080/api/v1/orders'

  constructor(private http: HttpClient) { }

  initiateSecurePayment(checkoutToken: string,paymentMethod:string): Observable<any>{
    const request = { 
      checkoutToken: checkoutToken,
      paymentMethod:paymentMethod 
    };
    return this.http.post<any>(`${this.webShopOrderUrl}/checkout`, request);
  }

  getAvailableMethods(webShopId: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/webshop/${webShopId}/available-methods`);
  }


}
