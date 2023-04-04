package com.bank.simulator.domain;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KeepValidatedCard {

    private Map<String, Cards> keepCard;

    public KeepValidatedCard() {
        keepCard = new HashMap<>();
    }

    public synchronized void putCard(String token, Cards card){
        keepCard.put(token, card);
    }

    public Map<String, Cards> getKeepCard() {
        return keepCard;
    }

    public void removeCard(String token){
        keepCard.remove(token);
    }

}
