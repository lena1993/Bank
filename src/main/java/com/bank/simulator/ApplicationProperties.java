package com.bank.simulator;

import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

    public static final String ISSUER_NAME = "issuerName";
    public static final String PAN = "pan";
    public static final String NOT_VALID_CARD = "The card is invalid";
    public static final String WRONG_PIN = "The Pin is wrong";
    public static final String WRONG_FINGERPRINT = "The FingerPrint is wrong";
    public static final String FAIL_GET_BALANCE = "Fail to show balance";
    public static final String CARD_TOKEN = "cardToken";
    public static final String BALANCE = "balance";
    public static final String FAIL_GET_MONEY = "Fail to get money";

}
