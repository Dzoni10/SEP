import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.css']
})
export class AdminPanelComponent implements OnInit {

  paymentMethods = [
    { id: 'CARD', name: 'Credit/Debit card', selected: false, icon: 'credit_card' },
    { id: 'QR', name: 'QR code (IPS NBS)', selected: false, icon: 'qr_code_2' },
    { id: 'PAYPAL', name: 'PayPal', selected: false, icon: 'account_balance_wallet' },
    { id: 'CRYPTO', name: 'Crypto', selected: false, icon: 'currency_bitcoin' }
  ];

  isLoading = false;

  constructor(private authService: AuthService,private snackBar:MatSnackBar){}

  ngOnInit(): void {
    this.loadSavedMethods();  
  }

  loadSavedMethods() {
    this.authService.getSavedPaymentMethods().subscribe({
      next: (savedMethods: string[]) => {
        if (savedMethods && savedMethods.length > 0) {
         
          this.paymentMethods.forEach(method => {
             if (savedMethods.includes(method.id)) {
              method.selected = true;
            }
          });
        }
      },
      error: (err) => {
        console.error("Failed to load saved payment methods", err);
      }
    });
  }

  saveConfiguration() {
    this.isLoading = true;
    
    const selectedMethods = this.paymentMethods
      .filter(m => m.selected)
      .map(m => m.id);

    if (selectedMethods.length === 0) {
      this.snackBar.open('You must choose at least one payment method!', 'Close', { duration: 3000 });
      this.isLoading = false;
      return;
    }

    this.authService.subscribeToPaymentMethods(selectedMethods).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.snackBar.open('Success saved payment method', 'Close', { 
          duration: 3000,
          panelClass: ['green-snackbar']
        });
      },
      error: (err) => {
        this.isLoading = false;
        this.snackBar.open('Error during save configuration', 'Close', { duration: 3000 });
      }
    });
  }
}
