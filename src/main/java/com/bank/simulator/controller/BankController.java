package com.bank.simulator.controller;

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


    @Autowired
    private BankService bankService;


    @PostMapping("/checkCard")
    public ResponseEntity checkCardExistenceInDb(@RequestBody Card card) {

        ResponseEntity response = bankService.checkCardExistence(card);
        if(response.getStatusCode() == HttpStatus.OK){
            return new ResponseEntity(response.getBody(), HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "message5");
    }

    @PostMapping("/checkPin")
    public ResponseEntity checkPin(@RequestBody Map<String, String> param) {
        ResponseEntity response = bankService.getTokenByPin(param.get("authenticationType"),
                param.get("pan"), param.get("pinOrFingerprint"));

        if(response.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity(response.getBody(), HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "message5");
    }

    /*@PostMapping("/checkFingerprint")
    public ResponseEntity checkFingerprint(@RequestBody Card card) {
        ResponseEntity response = bankService.fingerprintChecking(card);

        if(response.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity(response.getBody(), HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "message5");
    }
*/
    @PostMapping("/checkBalance")
    public ResponseEntity checkBalance(@RequestBody Map<String, String> param) {

        ResponseEntity response = bankService.getBalance(param.get("token"));

        if(response.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity(response.getBody(), response.getHeaders(), HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "message5");
    }

    @PostMapping("/cashOut")
    public ResponseEntity cashOut(@RequestBody Map<String, String> params) {

        ResponseEntity response = bankService.withdrawMoney(params.get("token"), Integer.parseInt(params.get("amount")));

        if(response.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity(response.getBody(), HttpStatus.OK);
        }

        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "message5");
    }
}
