package com.bank.simulator.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bank.simulator.MessagesProperties;
import com.bank.simulator.domain.ValidatedCardStorage;
import com.bank.simulator.domain.Cards;
import com.bank.simulator.dto.CardBalance;
import com.bank.simulator.dto.CardData;
import com.bank.simulator.dto.Token;
import com.bank.simulator.model.AuthType;
import com.bank.simulator.repos.CardRepo;
import com.bank.simulator.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Instant;
import java.util.*;

@Service
public class BankService {
    private final String PIN = "PIN";
    private final String CARD_TOKEN = "cardToken";
    private final String FINGERPRINT = "FINGERPRINT";
    private final String CARD = "card";
    private final String CARD_UPPERCASE = "Card";
    private final String PAN = "pan";
    private final String CARD_DETAILS = "Card Details";


    @Autowired
    private ValidatedCardStorage validatedCardStorage;

    @Autowired
    private CardRepo cardRepo;

    @Autowired
    MessagesProperties messagesProperties;

    public ResponseEntity checkCardExistence(Card insertedCard) {
        Cards card = cardRepo.findByPan(insertedCard.getPan());

        if(card.getCardHolder().equals(insertedCard.getCardHolder()) && card.getExpiryDate().equals(insertedCard.getExpiryDate())
                && card.getIssuer().equals(insertedCard.getIssuer())){

//            Map<String, String> param = new HashMap<>();
//            param.put(messagesProperties.ISSUER_NAME, card.getIssuer());
//            param.put(messagesProperties.PAN, card.getPan());

            CardData cardData = new CardData();
            cardData.setPan(card.getPan());

            return new ResponseEntity(cardData, HttpStatus.OK);

           // return new ResponseEntity(param, HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.NOT_VALID_CARD);
    }

    public ResponseEntity getTokenByPinOrFingerprint(String authenticationType, String pan, String pinOrFingerprint) throws HttpServerErrorException {
        AuthType authType = AuthType.valueOf(authenticationType);

        ResponseEntity result = null;

        switch (authType){
            case PIN:
            case FINGERPRINT:
                result = checkPinOrFingerprint(pan, pinOrFingerprint);
                break;
            default:
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.WRONG_AUTHENTICATION);
        }

        return result;
    }

    private ResponseEntity checkPinOrFingerprint(String pan, String pinOrFingerprint) throws HttpServerErrorException{
        Cards card = cardRepo.findByPan(pan);

        if(card.getPin().equals(pinOrFingerprint) || card.getFingerprint().equals(pinOrFingerprint)) {
            String generatedToken = generateToken(card.getPan());
            validatedCardStorage.putCard(card.getPan(), generatedToken);

//            Map<String, String> param = new HashMap<>();
//            param.put(CARD_TOKEN, generatedToken);
            Token token = new Token();
            token.setPan(card.getPan());
            token.setToken(generatedToken);

            return new ResponseEntity(token, HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.WRONG_AUTHENTICATION);
    }


    public ResponseEntity getBalance(String token) throws HttpServerErrorException {
        Cards card = getCardByToken(token);

        Integer balance =  card.getBalance();

        if(validatedCardStorage.getValidatedCard().get(card.getPan())== null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.FAIL_GET_BALANCE);
        }

        if(balance == 0) {
           throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.FAIL_GET_BALANCE);
        }

       /* Map<String, String> param = new HashMap<>();
        param.put(messagesProperties.CARD_TOKEN, token);
        param.put(messagesProperties.BALANCE, balance.toString());*/

        CardBalance sendCardData = new CardBalance();
        sendCardData.setCardPan(card.getPan());
        sendCardData.setCardBalance(card.getBalance());

        return new ResponseEntity(sendCardData, HttpStatus.OK);
    }

    public ResponseEntity<Object> withdrawMoney(String token, Integer amount) {
        Cards card = getCardByToken(token);
        Integer cardBalance =  card.getBalance();

        if(validatedCardStorage.getValidatedCard().get(card.getPan())==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.FAIL_GET_BALANCE);
        }

        if(card.getBalance()<amount){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.FAIL_GET_MONEY);
        }

        Integer balance = cardBalance - amount;
        card.setBalance(balance);
        cardRepo.save(card);

        validatedCardStorage.removeCard(card.getPan());

        /*Map<String, String> param = new HashMap<>();
        param.put(messagesProperties.PAN, card.getPan());
        param.put(messagesProperties.BALANCE, balance.toString());*/

        CardBalance sendCardData = new CardBalance();
        sendCardData.setCardPan(card.getPan());
        sendCardData.setCardBalance(card.getBalance());

//        JsonObject j=  new JsonObject();
//        j.add("cardData", cardData.);

        return new ResponseEntity(sendCardData, HttpStatus.OK);
    }

    private Cards getCardByToken(String token){
        Algorithm algorithm = Algorithm.HMAC256(CARD);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(CARD_UPPERCASE)
                .build();
        DecodedJWT decodedJWT = verifier.verify(token);

        String pan = ((Claim) decodedJWT.getClaim(PAN)).asString();

        Cards card = cardRepo.findByPan(pan);

        return card;
    }

    private String generateToken(String pan) {
        Algorithm algorithm = Algorithm.HMAC256(CARD);

        Instant now = Instant.now();
        String jwtToken = JWT.create()
                .withIssuer(CARD_UPPERCASE)
                .withSubject(CARD_DETAILS)
                .withClaim(PAN, pan)
                .withIssuedAt(now)
                .withExpiresAt(now.plusSeconds(120))
                .sign(algorithm);

        return jwtToken;
    }
}
