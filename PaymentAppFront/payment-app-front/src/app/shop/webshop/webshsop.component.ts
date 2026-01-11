import { Component, OnInit } from '@angular/core';
import { Car } from '../model/Car.model';
import { CarService } from '../car.service';
import { AuthService } from 'src/app/auth/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { ImageDialogComponent } from '../image-dialog/image-dialog.component';

@Component({
  selector: 'app-webshsop',
  templateUrl: './webshop.component.html',
  styleUrls: ['./webshop.component.css']
})
export class WebShopComponent implements OnInit {

  cars: Car[]=[]
  userId!:number;

  constructor(private carService:CarService,private auth: AuthService,private dialog:MatDialog){}


  ngOnInit(): void {
    this.userId = this.auth.getCurrentUser()?.userId!;
    this.carService.getAllCars().subscribe(res=>this.cars = res);
  }

  rent(carId: number){
    console.log('User', this.userId,'wants car',carId);
    //bekend
  }


  openImage(url: string) {
  this.dialog.open(ImageDialogComponent, {
    data: { url: url },
    panelClass: 'custom-dialog-container' // Opciono za stilizaciju pozadine
  });
  }
}
