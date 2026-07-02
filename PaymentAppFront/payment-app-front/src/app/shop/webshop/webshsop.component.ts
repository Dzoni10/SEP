import { Component, OnInit } from '@angular/core';
import { Car } from '../model/Car.model';
import { CarService } from '../car.service';
import { AuthService } from 'src/app/auth/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { ImageDialogComponent } from '../image-dialog/image-dialog.component';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-webshsop',
  templateUrl: './webshop.component.html',
  styleUrls: ['./webshop.component.css']
})
export class WebShopComponent implements OnInit {

  cars: Car[]=[]
  userId!:number;

  constructor(private http:HttpClient,private carService:CarService,private auth: AuthService,private dialog:MatDialog){}


  ngOnInit(): void {
    this.userId = this.auth.getCurrentUser()?.userId!;
    this.carService.getAllCars().subscribe(res=>this.cars = res);
  }

  rent(car:any){
    
    if(!car.selectedDays || car.selectedDays<1 || car.selectedDays>15){
      return;
    }

    const request = {
      carId: car.id,
      userId: this.userId,
      rentalDays: car.selectedDays,
      hasInsurance: car.insuranceSelected || false
    }

    this.http.post<any>('https://localhost:8080/api/v1/orders/initiate',request,{headers:this.auth.getAuthHeaders()}).subscribe({
    
      next: (response)=>{
        const paymentUrl = `https://localhost:4300/payment?token=${response.checkoutToken}`;
        window.location.href = paymentUrl;
      },
      error: (err)=>{
        console.error("Error during initialization order",err);
      }
    });
  }


  openImage(url: string) {
  this.dialog.open(ImageDialogComponent, {
    data: { url: url },
    panelClass: 'custom-dialog-container'
  });
  }
}
