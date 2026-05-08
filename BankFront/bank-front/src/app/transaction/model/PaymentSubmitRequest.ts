export interface PaymentSubmitRequest{
    pan:string;
    securityCode:string;
    cardHolderName:string;
    expirationDate:string;
}