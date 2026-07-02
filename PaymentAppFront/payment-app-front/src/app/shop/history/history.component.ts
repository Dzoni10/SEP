import { Component } from '@angular/core';
import { Car } from '../model/Car.model';
import { HttpClient } from '@angular/common/http';
import { AuthService } from 'src/app/auth/auth.service';
import { CarService } from '../car.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent {

  orders: any[] = [];
  cars: Car[] = [];
  userId!: number;

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private carService: CarService
  ) {}

  ngOnInit(): void {
    const user = this.auth.getCurrentUser();
    if (user && user.userId) {
      this.userId = user.userId;
      this.loadHistory();
    }
  }

  loadHistory() {
    this.carService.getAllCars().subscribe(cars => {
      this.cars = cars;
      this.http.get<any[]>(`https://localhost:8080/api/v1/orders/user/${this.userId}`, { headers: this.auth.getAuthHeaders() })
        .subscribe(orders => {
          this.orders = orders.filter(o => o.status === 'PAID');
        });
    });
  }

  getCarDetails(carId: number): string {
    const car = this.cars.find(c => c.id === carId);
    return car ? `${car.mark} ${car.model || ''}` : 'Unknown Car';
  }

  getRentalDays(carId: number, paidPrice: number): number | string {
    const car = this.cars.find(c => c.id === carId);
    if (!car) return '?';

    const basePrice = car.rentPrice;
    const daysWithoutInsurance = Math.round(paidPrice / basePrice);
    const priceWithInsurance = basePrice * 1.1;
    const daysWithInsurance = Math.round(paidPrice / priceWithInsurance);

    if (Math.abs((daysWithoutInsurance * basePrice) - paidPrice) < 2) {
      return daysWithoutInsurance;
    } else {
      return daysWithInsurance;
    }
  }

  checkInsurance(carId: number, paidPrice: number): string {
    const car = this.cars.find(c => c.id === carId);
    if (!car) return 'No';

    const basePrice = car.rentPrice;
    const days = Number(this.getRentalDays(carId, paidPrice));
    
    if (isNaN(days)) return 'No';
    const priceWithoutInsurance = basePrice * days;
    if (paidPrice - priceWithoutInsurance > 2) {
      return '✔️ Included (+10%)';
    }
    return '❌ Not included';
  }


}
