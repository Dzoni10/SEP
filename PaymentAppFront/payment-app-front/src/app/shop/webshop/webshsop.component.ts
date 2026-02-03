import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Car } from '../model/Car.model';
import { CarService } from '../car.service';
import { OrderService } from '../order.service';
import { AuthService } from 'src/app/auth/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ImageDialogComponent } from '../image-dialog/image-dialog.component';

@Component({
  selector: 'app-webshsop',
  templateUrl: './webshop.component.html',
  styleUrls: ['./webshop.component.css']
})
export class WebShopComponent implements OnInit {

  cars: Car[] = [];
  userId!: number;
  loading = false;

  constructor(
    private carService: CarService,
    private orderService: OrderService,
    private auth: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.userId = this.auth.getCurrentUser()?.userId!;
    this.carService.getAllCars().subscribe(res => this.cars = res);

    // Provera da li je korisnik upravo završio plaćanje
    const paymentStatus = this.route.snapshot.queryParamMap.get('payment') || '';
    const transactionId = this.route.snapshot.queryParamMap.get('transactionId');
    if (paymentStatus.startsWith('success') || transactionId) {
      this.snackBar.open('Plaćanje je uspešno izvršeno!', 'Zatvori', { duration: 5000 });
    } else if (paymentStatus.startsWith('failed')) {
      this.snackBar.open('Plaćanje nije uspelo.', 'Zatvori', { duration: 5000 });
    } else if (paymentStatus.startsWith('error')) {
      this.snackBar.open('Došlo je do greške pri plaćanju.', 'Zatvori', { duration: 5000 });
    }
  }

  rent(car: Car): void {
    if (this.loading) return;
    this.loading = true;

    this.orderService.checkout({
      items: [{ carId: car.id, price: car.rentPrice, rentalDays: 1 }],
      userId: this.userId,
      paymentMethod: 'CARD'
    }).subscribe({
      next: (response) => {
        this.loading = false;
        if (response.redirectUrl) {
          window.location.href = response.redirectUrl;
        } else {
          this.snackBar.open('Payment initiation failed', 'Close', { duration: 3000 });
        }
      },
      error: (err) => {
        this.loading = false;
        this.snackBar.open(err.error?.message || 'Checkout failed', 'Close', { duration: 5000 });
      }
    });
  }

  openImage(url: string): void {
    this.dialog.open(ImageDialogComponent, {
      data: { url: url },
      panelClass: 'custom-dialog-container'
    });
  }
}
