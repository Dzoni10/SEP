import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class PaymentGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    
    const txId = route.queryParamMap.get('txId');
    const token = route.queryParamMap.get('token');
    const transactionId = route.queryParamMap.get('transactionId');

    if(txId && txId.trim() !== '' || token && token.trim() !== '' || transactionId && transactionId.trim() !== ''){
        return true;
    }
    this.router.navigate(['/webshop']);
    return false;
  }
}