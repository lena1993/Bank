package com.bank.simulator.service;

import com.bank.simulator.ApplicationProperties;
import com.bank.simulator.domain.ValidatedCardStorage;
import com.bank.simulator.CardAuthentication;
import com.bank.simulator.domain.Cards;
import com.bank.simulator.repos.CardRepo;
import com.bank.simulator.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.*;

@Service
public class BankService {


    @Autowired
    private CardAuthentication cardAuthentication;

    @Autowired
    private ValidatedCardStorage validatedCardStorage;

    @Autowired
    private CardRepo cardRepo;

    @Autowired
    ApplicationProperties applicationProperties;

    public ResponseEntity checkCardExistence(Card insertedCard) {
        Iterable<Cards> cards = cardRepo.findAll();

        for (Cards card:cards) {
            if(card.getPan().equals(insertedCard.getPan()) && card.getCardHolder().equals(insertedCard.getCardHolder()) &&
                    card.getExpiryDate().equals(insertedCard.getExpiryDate()) && card.getIssuer().equals(insertedCard.getIssuer())) {

                Map<String, String> param = new HashMap<>();
                param.put(applicationProperties.ISSUER_NAME, card.getIssuer());
                param.put(applicationProperties.PAN, card.getPan());

                return new ResponseEntity(param, HttpStatus.OK);
            }
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.NOT_VALID_CARD);
    }

    //public ResponseEntity getTokenByPin(Card insertedCard) throws HttpServerErrorException {
    public ResponseEntity getTokenByPin(String authenticationType, String pan, String pinOrFingerprint) throws HttpServerErrorException {

        Cards card = cardRepo.findByPan(pan);

        if(authenticationType.equals("PIN") && card.getPin().equals(pinOrFingerprint)) {
            String generatedToken = generateToken(card.getPin(), card.getPan());
            validatedCardStorage.putCard(generatedToken, card);

            Map<String, String> param = new HashMap<>();
            param.put("cardToken", generatedToken);

            return new ResponseEntity(param, HttpStatus.OK);

        }else if(authenticationType.equals("FINGERPRINT") && card.getFingerprint().equals(pinOrFingerprint)) {
            String generatedToken = generateToken(card.getFingerprint(), card.getPan());
            validatedCardStorage.putCard(generatedToken, card);

            Map<String, String> param = new HashMap<>();
            param.put("cardToken", generatedToken);

            return new ResponseEntity(param, HttpStatus.OK);

        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.WRONG_PIN);
    }



    public String generateToken(String pin, String pan) {
        int random_int = (int)Math.floor(Math.random() * (15 - 10 + 1) + 10);

        int pinHashCode = pin.hashCode() * random_int;
        int panHashCode = pan.hashCode() * random_int;

        Integer token =  pinHashCode + panHashCode;

        if(token<0) {
            token = token * (-1);
        }

        return token.toString();
    }

    public ResponseEntity getBalance(String token) throws HttpServerErrorException {
       Integer balance = Integer.valueOf(validatedCardStorage.getValidatedCard().get(token).getBalance());

        if(balance == 0) {
           throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.FAIL_GET_BALANCE);
        }

        Map<String, String> param = new HashMap<>();
        param.put(applicationProperties.CARD_TOKEN, token);
        param.put(applicationProperties.BALANCE, balance.toString());

        return new ResponseEntity( param, HttpStatus.OK);
    }

    public ResponseEntity withdrawMoney(String token, Integer amount) {
        Integer cardBalance = validatedCardStorage.getValidatedCard().get(token).getBalance();

        if(cardBalance < amount) {
            validatedCardStorage.removeCard(token);

            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.FAIL_GET_MONEY);
        }

        Integer balance = cardBalance - amount;

        validatedCardStorage.getValidatedCard().get(token).setBalance(balance);

        Optional<Cards> cards = cardRepo.findById(validatedCardStorage.getValidatedCard().get(token).getId());
        cards.get().setBalance(balance);
        Cards ret = cardRepo.save(cards.get());

        Map<String, String> param = new HashMap<>();
        param.put(applicationProperties.PAN, validatedCardStorage.getValidatedCard().get(token).getPan());
        param.put(applicationProperties.BALANCE, balance.toString());

        return new ResponseEntity(param, HttpStatus.OK);
    }
}
