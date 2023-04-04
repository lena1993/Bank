package com.bank.simulator;

import com.bank.simulator.domain.Cards;
import com.bank.simulator.exception.*;
import com.bank.simulator.exception.*;
import com.bank.simulator.model.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CardAuthentication {

    private static Logger log = LoggerFactory.getLogger(CardAuthentication.class);

    public boolean isAuthenticated(Cards bankCard, Card atmCard){
        try {
            validatePin(bankCard, atmCard);
            validatePan(bankCard, atmCard);
            expirationValidationDate(bankCard, atmCard);
            ValidateCardHolder(bankCard, atmCard);
            ValidateCardType(bankCard, atmCard);
            ValidateIssuer(bankCard, atmCard);

            log.info("Everything is Ok, The card is valid. Method isAuthenticated() {}", CardAuthentication.class);

        } catch (Exception e) {

            log.error("The card isn't valid! Method isAuthenticated!", e);

            return false;
        }
        return true;
    }

    private void validatePin(Cards bankCard, Card atmCard) throws PinNotValidException {
        if(!bankCard.getPin().equals(atmCard.getPin())){
            log.info("Invalid Pin code. Method validatePin() {}", CardAuthentication.class);
            throw new PinNotValidException("Your Pin is not correct");
        }
    }

    private void validatePan(Cards bankCard, Card atmCard) throws PanNotValidException {
        if(!bankCard.getPan().equals(atmCard.getPan())){
            log.info("Invalid Pan. Method validatePan() {}", CardAuthentication.class);
            throw new PanNotValidException("Your Pan is not correct");
        }
    }

    private void expirationValidationDate(Cards bankCard, Card atmCard) throws CardExpiredDateNotValid {
        if(!bankCard.getExpiryDate().equals(atmCard.getExpiryDate())){
            log.info("Invalid ExpiryDate. Method getExpiryDate() {}", CardAuthentication.class);
            throw new CardExpiredDateNotValid("Your expiry date is not correct");
        }
    }

    private void ValidateCardHolder(Cards bankCard, Card atmCard) throws CardHolderNotValid {
        if(!bankCard.getCardHolder().equals(atmCard.getCardHolder())){
            log.info("Invalid Card Holder. Method getCardHolder() {}", CardAuthentication.class);
            throw new CardHolderNotValid("Card Holder name is not correct");
        }
    }

    private void ValidateCardType(Cards bankCard, Card atmCard) throws CardTypeNotValid {
        if(!bankCard.getCardType().equals(atmCard.getCardType())){
            log.info("Invalid Card Type. Method getCardType() {}", CardAuthentication.class);
            throw new CardTypeNotValid("Card type is not correct");
        }
    }

   private void ValidateIssuer(Cards bankCard, Card atmCard) throws IssuerNotValid {
        if(!bankCard.getIssuer().equals(atmCard.getIssuer())){
            log.info("Invalid Issuer. Method getIssuer() {}", CardAuthentication.class);
            throw new IssuerNotValid("Issuer is not correct");
        }
    }

}
