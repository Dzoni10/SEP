export interface BankTransaction{
    paymentId: string;
    amount: number;
    currency: string;
    stan:string;
    pspTimestamp:Date;
    createdAt:Date;
    expiresAt:Date;
    attempsCount:number;
    status:string;
    globalTransactionId:string;
    acquirerTimestamp:Date;
    callbackUrl:string;
    successUrl:string;
    failedUrl:string;
    errorUrl:string;
}