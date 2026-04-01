export interface PaymentResponse {
  success: boolean;
  redirectUrl: string;
  transactionId: string;
  errorMessage: string;
}