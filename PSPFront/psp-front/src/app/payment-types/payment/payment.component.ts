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

  constructor(private route: ActivatedRoute,private snackBar:MatSnackBar){}

  ngOnInit(): void {
    // Uzimamo podatke iz URL-a koje je poslala prva aplikacija
    const carIdParam = this.route.snapshot.queryParamMap.get('carId');
    const userIdParam = this.route.snapshot.queryParamMap.get('userId');

    if (carIdParam) {
      this.carId = +carIdParam; // '+' pretvara string "1" u broj 1
    }

    if (userIdParam) {
      this.userId = Number(userIdParam); // Drugi način konverzije
    }
    
    console.log('Rent payment:', this.carId, 'User:', this.userId);
  }

  selectPayment(method: string) {
    alert(`You choose payment with: ${method.toUpperCase()} for car: ${this.carId}`);
    this.snackBar.open(`You choose payment with: ${method.toUpperCase()} for car: ${this.carId}`, "Close", {
            duration: 4000,
            horizontalPosition: "center"
    });
  }

}
