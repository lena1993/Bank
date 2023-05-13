package com.bank.simulator.service;

import com.bank.simulator.model.Card;
import com.bank.simulator.repos.CardRepo;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {

    private final String PAN = "pan";
    private final String TOKEN = "token";
    private final String BALANCE = "balance";

    private final String PAN_VALUE = "1234567891234567";
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
    BankService bankService;

    @Mock
    CardRepo cardRepo;

    @Test
    void checkCardExistence() {
//        Card card = new Card(null,PAN_VALUE, EXPIRY_DATE, CARD_HOLDER, CARD_TYPE, ISSUER);
//        Mockito.when(cardRepo.findByPan(PAN_VALUE)).thenReturn(card);
    }

    @Test
    void getTokenByPinOrFingerprint() {
    }

    @Test
    void getBalance() {
    }

    @Test
    void withdrawMoney() {
    }
}