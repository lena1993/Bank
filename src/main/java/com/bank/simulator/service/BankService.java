package com.bank.simulator.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
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

import java.time.Instant;
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

        Cards card = cardRepo.findByPan(pan);

        if(authenticationType.equals("PIN") && card.getPin().equals(pinOrFingerprint)) {
            String generatedToken = generateToken(card.getPin(), card.getPan());
            validatedCardStorage.putCard(card.getPan(), generatedToken);

            Map<String, String> param = new HashMap<>();
            param.put("cardToken", generatedToken);

            return new ResponseEntity(param, HttpStatus.OK);

        }else if(authenticationType.equals("FINGERPRINT") && card.getFingerprint().equals(pinOrFingerprint)) {
            String generatedToken = generateToken(card.getFingerprint(), card.getPan());
            validatedCardStorage.putCard(card.getPan(), generatedToken);

            Map<String, String> param = new HashMap<>();
            param.put("cardToken", generatedToken);

            return new ResponseEntity(param, HttpStatus.OK);

        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, applicationProperties.WRONG_AUTHENTICATION);
    }

    public ResponseEntity getBalance(String token) throws HttpServerErrorException {
        Cards card = getCardByToken(token);

        Integer balance =  card.getBalance();

        if(validatedCardStorage.getValidatedCard().get(card.getPan())==null){
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
        Algorithm algorithm = Algorithm.HMAC256("card");

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("Card")
                .build();
        DecodedJWT decodedJWT = verifier.verify(token);

        String pan = ((Claim) decodedJWT.getClaim("pan")).asString();

        Cards card = cardRepo.findByPan(pan);

        return card;
    }

    private String generateToken(String pin, String pan) {
        Algorithm algorithm = Algorithm.HMAC256("card");

        Instant now = Instant.now();
        String jwtToken = JWT.create()
                .withIssuer("Card")
                .withSubject("Card Details")
                .withClaim("pan", pan)
                .withIssuedAt(now)
                .withExpiresAt(now.plusSeconds(120))
                .sign(algorithm);

        return jwtToken;

    }
}
