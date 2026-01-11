import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';
import { Car } from './model/Car.model';

@Injectable({
  providedIn: 'root'
})
export class CarService {

  private apiUrl = 'http://localhost:8080/api/v1/cars';

  constructor(private http: HttpClient, private auth: AuthService) { }

  getAllCars(): Observable<Car[]> {
    return this.http.get<Car[]>(this.apiUrl, {headers:this.auth.getAuthHeaders()});
  }
}
