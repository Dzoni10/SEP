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
    
    // Filtriramo samo one koji su 'selected: true' i uzimamo njihov 'id' (CARD, QR...)
    const selectedMethods = this.paymentMethods
      .filter(m => m.selected)
      .map(m => m.id);

    if (selectedMethods.length === 0) {
      this.snackBar.open('Morate izabrati barem jedan način plaćanja!', 'Zatvori', { duration: 3000 });
      this.isLoading = false;
      return;
    }

    // Šaljemo izabrane metode na beku
    this.authService.subscribeToPaymentMethods(selectedMethods).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.snackBar.open('Uspešno sačuvane metode plaćanja!', 'Zatvori', { 
          duration: 3000,
          panelClass: ['green-snackbar'] // Možeš dodati custom CSS klasu za zelenu boju
        });
      },
      error: (err) => {
        this.isLoading = false;
        this.snackBar.open('Greška pri čuvanju konfiguracije.', 'Zatvori', { duration: 3000 });
      }
    });
  }
}
