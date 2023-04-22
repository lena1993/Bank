package com.bank.simulator.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bank.simulator.ApplicationProperties;
import com.bank.simulator.domain.ValidatedCardStorage;
import com.bank.simulator.domain.Cards;
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
    ApplicationProperties applicationProperties;

    public ResponseEntity checkCardExistence(Card insertedCard) {
        Cards card = cardRepo.findByPan(insertedCard.getPan());

        if(card.getCardHolder().equals(insertedCard.getCardHolder()) && card.getExpiryDate().equals(insertedCard.getExpiryDate())
                && card.getIssuer().equals(insertedCard.getIssuer())){

            Map<String, String> param = new HashMap<>();
            param.put(applicationProperties.ISSUER_NAME, card.getIssuer());
            param.put(applicationProperties.PAN, card.getPan());

            return new ResponseEntity(param, HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.NOT_VALID_CARD);
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
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.WRONG_AUTHENTICATION);
        }

        return result;
    }

    private ResponseEntity checkPinOrFingerprint(String pan, String pinOrFingerprint) throws HttpServerErrorException{
        Cards card = cardRepo.findByPan(pan);

        if(card.getPin().equals(pinOrFingerprint) || card.getFingerprint().equals(pinOrFingerprint)) {
            String generatedToken = generateToken(card.getPan());
            validatedCardStorage.putCard(card.getPan(), generatedToken);

            Map<String, String> param = new HashMap<>();
            param.put(CARD_TOKEN, generatedToken);

            return new ResponseEntity(param, HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.WRONG_AUTHENTICATION);
    }


    public ResponseEntity getBalance(String token) throws HttpServerErrorException {
        Cards card = getCardByToken(token);

        Integer balance =  card.getBalance();

        if(validatedCardStorage.getValidatedCard().get(card.getPan())== null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.FAIL_GET_BALANCE);
        }

        if(balance == 0) {
           throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.FAIL_GET_BALANCE);
        }

        Map<String, String> param = new HashMap<>();
        param.put(applicationProperties.CARD_TOKEN, token);
        param.put(applicationProperties.BALANCE, balance.toString());

        return new ResponseEntity(param, HttpStatus.OK);
    }

    public ResponseEntity<Object> withdrawMoney(String token, Integer amount) {
        Cards card = getCardByToken(token);
        Integer cardBalance =  card.getBalance();

        if(validatedCardStorage.getValidatedCard().get(card.getPan())==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.FAIL_GET_BALANCE);
        }

        if(card.getBalance()<amount){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.FAIL_GET_MONEY);
        }

        Integer balance = cardBalance - amount;
        card.setBalance(balance);
        cardRepo.save(card);

        validatedCardStorage.removeCard(card.getPan());

        Map<String, String> param = new HashMap<>();
        param.put(applicationProperties.PAN, card.getPan());
        param.put(applicationProperties.BALANCE, balance.toString());

        return new ResponseEntity(param, HttpStatus.OK);
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
