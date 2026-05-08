import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PaymentTypesService } from '../payment-types.service';
import { PaymentInitiationRequest } from '../model/PaymentInitRequest';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent  implements OnInit{

  carId: number | null = null;
  userId: number | null=null;
  rentalDays: number | null=null;
  token: string | null=null;

  availableMethods: string[] = [];

  constructor(private route: ActivatedRoute,private snackBar:MatSnackBar, private paymentService:PaymentTypesService){}

  ngOnInit(): void {
   
    const tokenParam = this.route.snapshot.queryParamMap.get('token');
    if(tokenParam) this.token = tokenParam;
    this.loadAvailableMethods(1);
  }

  loadAvailableMethods(webShopId: number) {
    this.paymentService.getAvailableMethods(webShopId).subscribe({
      next: (methods) => {
        // Pretvaramo sve u velika slova radi lakšeg upoređivanja (npr. "CARD", "QR")
        this.availableMethods = methods.map(m => m.toUpperCase());
      },
      error: (err) => {
        console.error("Failed to load methods", err);
      }
    });
  }

  selectPayment(method: string) {

    const paymentToken = Math.random().toString(36).substring(2) + Date.now().toString(36);
    
    sessionStorage.setItem('paymentSession', paymentToken);

    if(!this.token){
      this.snackBar.open("Error: invalid payment session", "Close",{duration:3000});
      return;
    }

    this.paymentService.initiateSecurePayment(this.token,method.toUpperCase()).subscribe({
      next: (response) => {
        if (response.status==='success' && response.redirectUrl) {
           window.location.href = response.redirectUrl; 
        } else {
          this.snackBar.open('Error: ' + (response.errorMessage || 'Payment denied'), "Close", { duration: 3000 });
        }
      },
      error: (err) => {
        this.snackBar.open('Server error during payment init.', "Close", { duration: 3000 });
      }
    });
  }
}
