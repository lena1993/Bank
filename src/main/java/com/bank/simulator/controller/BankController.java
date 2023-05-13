package com.bank.simulator.controller;

import com.bank.simulator.MessagesProperties;
import com.bank.simulator.dto.CardBalance;
import com.bank.simulator.dto.CardData;
import com.bank.simulator.dto.Token;
import com.bank.simulator.service.BankService;

import io.swagger.annotations.Api;
import com.bank.simulator.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;

@RestController
@RequestMapping("/bank/")
@Api(value = "Bank Controller")
public class BankController {

    private final String CHECK_CARD = "/checkCard";
    private final String CHECK_PIN = "/checkPin";
    private final String CHECK_BALANCE = "/checkBalance";
    private final String CASH_OUT = "/cashOut";

    private final String AUTHENTICATION_TYPE = "authenticationType";
    private final String PAN = "pan";
    private final String PIN_OR_FINGERPRINT = "pinOrFingerprint";
    private final String TOKEN = "token";
    private final String AMOUNT = "amount";

    @Autowired
    private BankService bankService;

    @Autowired
    MessagesProperties messagesProperties;


    @PostMapping(CHECK_CARD)
    public ResponseEntity checkCardExistenceInDb(@RequestBody Card card) {

        CardData response = bankService.checkCardExistence(card);
        if(response != null){
            return new ResponseEntity(response, HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.CARD_NOT_FOUND);
    }

    @PostMapping(CHECK_PIN)
    public ResponseEntity checkPin(@RequestBody Map<String, String> param) {
        Token response = bankService.getTokenByPinOrFingerprint(param.get(AUTHENTICATION_TYPE),
                param.get(PAN), param.get(PIN_OR_FINGERPRINT));

        if(response != null) {
            return new ResponseEntity(response, HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.WRONG_AUTHENTICATION);
    }


    @PostMapping(CHECK_BALANCE)
    public ResponseEntity checkBalance(@RequestBody Map<String, String> param) {

        CardBalance response = bankService.getBalance(param.get(TOKEN));

        if(response != null) {
            return new ResponseEntity(response, HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.FAIL_GET_BALANCE);
    }

    @PostMapping(CASH_OUT)
    public ResponseEntity<Object> cashOut(@RequestBody Map<String, String> params) {

        CardBalance response = bankService.withdrawMoney(params.get(TOKEN), Integer.parseInt(params.get(AMOUNT)));

        if(response != null) {
            return new ResponseEntity(response, HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, messagesProperties.FAIL_GET_MONEY);
    }
}
