package com.bank.simulator.domain;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ValidatedCardStorage {

    private Map<String, String> panOfTheCard;

    public ValidatedCardStorage() {
        panOfTheCard = Collections.synchronizedMap(new HashMap<>());
    }

    public synchronized void putCard(String pan, String token) {
        panOfTheCard.put(pan, token);
    }

    public Map<String, String> getValidatedCard() {
        return panOfTheCard;
    }

    public void removeCard(String pan) {
        panOfTheCard.remove(pan);
    }

}
