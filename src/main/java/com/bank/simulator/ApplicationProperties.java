package com.bank.simulator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:messages.properties")
public class ApplicationProperties {

    @Value("${ISSUER_NAME}")
    public String ISSUER_NAME;

    @Value("${PAN}")
    public String PAN;

    @Value("${NOT_VALID_CARD}")
    public String NOT_VALID_CARD;

    @Value("${WRONG_PIN}")
    public String WRONG_PIN;

    @Value("${WRONG_FINGERPRINT}")
    public String WRONG_FINGERPRINT;

    @Value("${FAIL_GET_BALANCE}")
    public String FAIL_GET_BALANCE;

    @Value("${CARD_TOKEN}")
    public String CARD_TOKEN;

    @Value("${BALANCE}")
    public String BALANCE;

    @Value("${FAIL_GET_MONEY}")
    public String FAIL_GET_MONEY;

}
