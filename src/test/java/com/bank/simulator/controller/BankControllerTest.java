package com.bank.simulator.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bank.simulator.dto.CardBalance;
import com.bank.simulator.dto.CardData;
import com.bank.simulator.dto.Token;
import com.bank.simulator.model.Card;
import com.bank.simulator.service.BankService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class})
class BankControllerTest {

    private final String PAN = "pan";
    private final String TOKEN = "token";
    private final String BALANCE = "balance";

    private final String PAN_VALUE = "1234567891234567";
    private final String PAN_VALUE_NOT_VALID = "1234567891234568";
    private final String PIN_VALUE = "1234";
    private final String EXPIRY_DATE = "02/24";
    private final String CARD_HOLDER = "Lena Sargsyan";
    private final String CARD_TYPE = "VISA";
    private final String ISSUER = "HSBC";
    private final String AUTHENTICATION_TYPE = "PIN";
    private final String AUTHENTICATION = "authenticationType";
    private final String PIN_OR_FINGERPRINT = "pinOrFingerprint";
    private final Integer BALANCE_VALUE = 100;
    private final Integer AMOUNT = 10;
    private final String AMOUNT_NAME = "amount";

    private final String CARD = "card";
    private final String CARD_UPPERCASE = "Card";
    private final String CARD_DETAILS = "Card Details";

    @InjectMocks
    BankController bankController;

    @Mock
    BankService bankService;

    @Test
    void checkCardExistenceInDb() {
        Card card = new Card(null, PAN_VALUE, EXPIRY_DATE, CARD_HOLDER, CARD_TYPE, ISSUER);

        CardData c = new CardData();
        c.setPan(card.getPan());

        Mockito.when(bankService.checkCardExistence(card)).thenReturn(c);

        ResponseEntity responseEntity = bankController.checkCardExistenceInDb(card);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(c);
    }

    @Test
    void checkCardExistenceInDbNotValid() {
        Card card = new Card(null, PAN_VALUE_NOT_VALID, EXPIRY_DATE, CARD_HOLDER, CARD_TYPE, ISSUER);

        CardData c = new CardData();
        c.setPan(card.getPan());

        Mockito.when(bankService.checkCardExistence(card)).thenThrow(HttpServerErrorException.class);

        assertThrows(HttpServerErrorException.class, () -> {
            bankController.checkCardExistenceInDb(card);
        });
    }

    @Test
    void checkPin() {

        String generatedToken = generateToken(PAN_VALUE);

        Token token = new Token();
        token.setToken(generatedToken);
        token.setPan(PAN_VALUE);

        Mockito.when(bankService.getTokenByPinOrFingerprint(AUTHENTICATION_TYPE, PAN_VALUE, PIN_VALUE)).
                thenReturn(token);

        Map<String, String> param = new HashMap<>();
        param.put(AUTHENTICATION, AUTHENTICATION_TYPE);
        param.put(PAN, PAN_VALUE);
        param.put(PIN_OR_FINGERPRINT, PIN_VALUE);

        ResponseEntity responseEntity = bankController.checkPin(param);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(token);

    }

    @Test
    void checkPinNotValid() {
        String generatedToken = generateToken(PAN_VALUE_NOT_VALID);

        Mockito.when(bankService.getTokenByPinOrFingerprint(AUTHENTICATION_TYPE, PAN_VALUE_NOT_VALID, PIN_VALUE)).
                thenThrow(HttpServerErrorException.class);

        Map<String, String> param = new HashMap<>();
        param.put(AUTHENTICATION, AUTHENTICATION_TYPE);
        param.put(PAN, PAN_VALUE_NOT_VALID);
        param.put(PIN_OR_FINGERPRINT, PIN_VALUE);

        assertThrows(HttpServerErrorException.class, () -> {
            bankController.checkPin(param);
        });
    }

    @Test
    void checkBalance() {
        String generatedToken = generateToken(PAN_VALUE);

        CardBalance sendCardData = new CardBalance();
        sendCardData.setCardPan(PAN_VALUE);
        sendCardData.setCardBalance(BALANCE_VALUE);
        sendCardData.setToken(generatedToken);

        Mockito.when(bankService.getBalance(generatedToken)).
                thenReturn(sendCardData);

        Map<String, String> param = new HashMap<>();
        param.put(TOKEN, generatedToken);

        ResponseEntity responseEntity = bankController.checkBalance(param);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(sendCardData);
    }

 /*   @Test
    void checkBalanceNotValid() {

        String tokenNotValid = "NOT_VALID_TOKEN";
        Mockito.when(bankService.getBalance(tokenNotValid)).thenThrow(null);

        Map<String, String> param = new HashMap<>();
        param.put(TOKEN, tokenNotValid);

        ResponseEntity responseEntity = bankController.checkBalance(param);

        assertThrows(HttpServerErrorException.class, () -> {
            bankController.checkBalance(param);
        });
    }*/

    @Test
    void cashOut() {
        String generatedToken = generateToken(PAN_VALUE);

        CardBalance sendCardData = new CardBalance();
        sendCardData.setCardPan(PAN_VALUE);
        sendCardData.setCardBalance(BALANCE_VALUE);
        sendCardData.setToken(null);

        Mockito.when(bankService.withdrawMoney(generatedToken, AMOUNT)).
                thenReturn(sendCardData);

        Map<String, String> param = new HashMap<>();
        param.put(TOKEN, generatedToken);
        param.put(AMOUNT_NAME, AMOUNT.toString());

        ResponseEntity responseEntity = bankController.cashOut(param);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(sendCardData);
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