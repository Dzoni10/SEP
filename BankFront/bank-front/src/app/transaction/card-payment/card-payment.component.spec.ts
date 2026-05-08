import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardPaymentComponent } from './card-payment.component';

describe('CardPaymentComponent', () => {
  let component: CardPaymentComponent;
  let fixture: ComponentFixture<CardPaymentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CardPaymentComponent]
    });
    fixture = TestBed.createComponent(CardPaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
