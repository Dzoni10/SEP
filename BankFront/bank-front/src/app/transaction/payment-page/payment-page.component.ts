import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-payment-page',
  templateUrl: './payment-page.component.html',
  styleUrls: ['./payment-page.component.css']
})
export class PaymentPageComponent implements OnInit {

  paymentId: string | null = null;
  selectedMethod: string='card';

  constructor(private route: ActivatedRoute){}

  ngOnInit(): void{
    this.route.paramMap.subscribe(params=>{
      this.paymentId = params.get('paymentId');
      console.log("Read payment id from url: ",this.paymentId);
    });
  }

  selectMethod(method: string){
    this.selectedMethod = method;
  }
}
