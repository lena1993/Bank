package com.bank.simulator.domain;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ValidatedCardStorage {

    private Map<String, Cards> validatedCard;

    public ValidatedCardStorage() {
        validatedCard = Collections.synchronizedMap(new HashMap<>());
    }

    public synchronized void putCard(String token, Cards card) {
        validatedCard.put(token, card);
    }

    public Map<String, Cards> getValidatedCard() {
        return validatedCard;
    }

    public void removeCard(String token) {
        validatedCard.remove(token);
    }

}
