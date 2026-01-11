import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent  implements OnInit{

  carId: number | null = null;
  userId: number | null = null;
  redirectUrl: string | null = null;

  constructor(private route: ActivatedRoute,private snackBar:MatSnackBar){}

  ngOnInit(): void {
    // Uzimamo podatke iz URL-a koje je poslala prva aplikacija
    const carIdParam = this.route.snapshot.queryParamMap.get('carId');
    const userIdParam = this.route.snapshot.queryParamMap.get('userId');
    this.redirectUrl = this.route.snapshot.queryParamMap.get('redirectUrl');

    if (carIdParam) {
      this.carId = +carIdParam; // '+' pretvara string "1" u broj 1
    }

    if (userIdParam) {
      this.userId = Number(userIdParam); // Drugi način konverzije
    }
    
    console.log('Rent payment:', this.carId, 'User:', this.userId, 'Redirect URL:', this.redirectUrl);
  }

  selectPayment(method: string) {
    if (method === 'card') {
      // Za card payment, treba da dobijemo paymentId iz URL-a ili query params
      // Za sada ćemo koristiti query params sa redirectUrl koji dolazi iz Web Shop-a
      const redirectUrl = this.route.snapshot.queryParamMap.get('redirectUrl');
      if (redirectUrl) {
        // Ako postoji redirectUrl, preusmeri direktno na njega
        window.location.href = redirectUrl;
      } else {
        this.snackBar.open('Redirect URL not found', "Close", {
          duration: 3000,
          horizontalPosition: "center"
        });
      }
    } else {
      // Za ostale metode plaćanja (QR, Crypto, PayPal)
      this.snackBar.open(`Payment method ${method.toUpperCase()} will be available soon`, "Close", {
        duration: 4000,
        horizontalPosition: "center"
      });
    }
  }

}
