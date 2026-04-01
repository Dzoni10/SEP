export interface PaymentInitiationRequest {
  merchantId: string;
  merchantPassword: string;
  amount: number;
  currency: string;
  merchantOrderId: string;
  merchantTimeStamp: string;
  successUrl: string;
  failedUrl: string;
  errorUrl: string;
  paymentMethod: string;
}