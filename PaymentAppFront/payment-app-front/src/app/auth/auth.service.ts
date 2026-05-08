import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { Register } from './model/Register.model';
import { DecodedToken } from './model/decodedToken';
import { AuthResponse } from './model/AuthResponse.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private currentUserSubject = new BehaviorSubject<DecodedToken | null>(this.loadUserFromToken());
  currentUser$ = this.currentUserSubject.asObservable();

  private apiUrl = 'https://localhost:8080/api/users';
  private adminUrl = 'https://localhost:8080/api/admin/payment-methods';

  constructor(private http: HttpClient) { }

  register(registration: Register): Observable<any>{
    return this.http.post(`${this.apiUrl}/signup`,registration);
  }

  login(email: string, password: string):  Observable<AuthResponse>{
    const body = {email, password};
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`,body)
  }

  subscribeToPaymentMethods(methods: string[]): Observable<any> {
    const request = { methods: methods, headers:this.getAuthHeaders() };
    return this.http.post(`${this.adminUrl}/subscribe`, request, { responseType: 'text' });
  }

  getSavedPaymentMethods(): Observable<string[]> {
    return this.http.get<string[]>(`${this.adminUrl}/current`, { headers: this.getAuthHeaders() });
  }

  getAuthHeaders(): HttpHeaders {
  const token = this.getToken();
  let headers = new HttpHeaders();
  if(token)
  {
    headers=headers.set('Authorization',`Bearer ${token}`);
  }
  return headers;
}

saveToken(token: string){
    localStorage.setItem('jwtToken',token)
    const decoded=jwtDecode<DecodedToken>(token);
    this.currentUserSubject.next(decoded);
  }

  getCurrentUser(): DecodedToken|null{
    return this.currentUserSubject.value;
  }

getToken():string|null{
    return localStorage.getItem('jwtToken')
  }

logout(){
    localStorage.removeItem('jwtToken');
    this.currentUserSubject.next(null);
}

private loadUserFromToken(): DecodedToken | null {
    const token = this.getToken();
    if(token){
      try{
        const decoded = jwtDecode<DecodedToken>(token);

        if(this.isTokenExpired(decoded)){
          this.logout();
          return null;
        }

        return decoded;
        }
      catch
          {
              return null;
          }
    }
    return null;
  }

  private isTokenExpired(decoded: DecodedToken): boolean{
    return decoded.exp*1000 < Date.now();
  }

}