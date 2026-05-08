package com.bankservice.bank.config;

import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
public class CardValidator {

    public boolean isValidLuhn(String pan){
        if(pan == null || !pan.matches("\\d+")){
            return false;
        }
        int sum=0;
        boolean alternate = false;

        for(int i=pan.length()-1; i>=0; i--){
            int n = Integer.parseInt(pan.substring(i,i+1));
            if(alternate){
                n*=2;
                if(n>9){
                    n=(n%10)+1;
                }
            }
            sum+=n;
            alternate = !alternate;
        }
        return (sum %10 ==0);
    }

    public boolean isValidExpirationDate(String expirationDate){
        if(expirationDate == null || !expirationDate.matches("(0[1-9]|1[0-2])/\\d{2}")){
            return false;
        }
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expiry= YearMonth.parse(expirationDate, formatter);
            YearMonth currentMonth = YearMonth.now();
            return !expiry.isBefore(currentMonth);
        }catch (Exception e){
            return false;
        }
    }
}
