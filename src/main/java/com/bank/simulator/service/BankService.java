package com.bank.simulator.service;

import com.bank.simulator.domain.KeepValidatedCard;
import com.bank.simulator.CardAuthentication;
import com.bank.simulator.domain.Cards;
import com.bank.simulator.repos.CardRepo;
import com.bank.simulator.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
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
    private KeepValidatedCard keepValidatedCard;

    @Autowired
    private CardRepo cardRepo;

    @Autowired
    MessageSource messageSource;


    public synchronized ResponseEntity checkCardExistence(Card insertedCard){

        Locale locale = LocaleContextHolder.getLocale();

        Iterable<Cards> cards = cardRepo.findAll();

        for (Cards card:cards) {

            if(card.getPan().equals(insertedCard.getPan()) && card.getCardHolder().equals(insertedCard.getCardHolder()) &&
                    card.getExpiryDate().equals(insertedCard.getExpiryDate()) && card.getIssuer().equals(insertedCard.getIssuer())){

                Map<String, String> param = new HashMap<>();
                //param.put("message1", messageSource.getMessage("correctCard",null, locale));
                param.put("issuerName", card.getIssuer());
                param.put("pan", card.getPan());

                //return new ResponseEntity(messageSource.getMessage("correctCard",null, locale) +
                  //      card.getIssuer() + messageSource.getMessage("card",null, locale), HttpStatus.OK);

                return new ResponseEntity(param, HttpStatus.OK);
            }
          /*  else{
                return new ResponseEntity(messageSource.getMessage("incorrectCard",null, locale) +
                        card.getIssuer() +  messageSource.getMessage("card",null, locale), HttpStatus.BAD_REQUEST);
            }*/
        }
        //return new ResponseEntity( messageSource.getMessage("incorrectCard",null, locale), HttpStatus.BAD_REQUEST);
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "message3");
    }

    public ResponseEntity checkPinCode(Card insertedCard) throws HttpServerErrorException{
        Locale locale = LocaleContextHolder.getLocale();

        Iterable<Cards> cards = cardRepo.findAll();

        for (Cards card:cards) {
            if(card.getPan().equals(insertedCard.getPan()) && card.getPin().equals(insertedCard.getPin())){

                String generatedToken = generateToken(card.getPin(), card.getPan());

                keepValidatedCard.putCard(generatedToken, card);

                //HttpHeaders headers = new HttpHeaders();
                //headers.add("cardToken", generatedToken);

                Map<String, String> param = new HashMap<>();
                param.put("cardToken", generatedToken);
                //param.put("card", card);


                //return new ResponseEntity(messageSource.getMessage("correctPinCode",null, locale) + card, headers, HttpStatus.OK);
                return new ResponseEntity(param, HttpStatus.OK);
            }

        }

        //return new ResponseEntity(messageSource.getMessage("incorrectPinOrPan",null, locale), HttpStatus.BAD_REQUEST);
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "message3");
    }


    public String generateToken(String pin, String pan){
        int random_int = (int)Math.floor(Math.random() * (15 - 10 + 1) + 10);

        int pinHashCode = pin.hashCode() * random_int;
        int panHashCode = pan.hashCode() * random_int;
        Integer token =  pinHashCode + panHashCode;

        if(token<0){
            token = token * (-1);
        }
        return token.toString();
    }

    public ResponseEntity getBalance(String token) throws HttpServerErrorException{


//        Integer balance =Integer.parseInt(keepValidatedCard.getKeepCard().get(token));

       Integer balance = Integer.valueOf(keepValidatedCard.getKeepCard().get(token).getBalance());

        if(balance == 0){

            //return new ResponseEntity( balance, HttpStatus.INTERNAL_SERVER_ERROR);

            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "message7");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("cardToken", token);

        Map<String, String> param = new HashMap<>();
        param.put("cardToken", token);
        param.put("balance", balance.toString());

        return new ResponseEntity( param, HttpStatus.OK);
    }

    public ResponseEntity withdrawMoney(String token, Integer amount) {
        List<Integer> result = new ArrayList<>();
        List<String> pan = new ArrayList<>();

        Integer cashedOut = 0;

        Integer cardBalance = keepValidatedCard.getKeepCard().get(token).getBalance();

        if(cardBalance < amount){
            keepValidatedCard.removeCard(token);
            //return new ResponseEntity( cardBalance , HttpStatus.INTERNAL_SERVER_ERROR);

            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "message8");
        }

        Integer balance = cardBalance - amount;
        keepValidatedCard.getKeepCard().get(token).setBalance(balance);

        Optional<Cards> cards = cardRepo.findById(keepValidatedCard.getKeepCard().get(token).getId());
        cards.get().setBalance(balance);
        Cards ret = cardRepo.save(cards.get());

        Map<String, String> param = new HashMap<>();
        param.put("pan", keepValidatedCard.getKeepCard().get(token).getPan());
        param.put("balance", balance.toString());

        return new ResponseEntity(param, HttpStatus.OK);


    }

}
