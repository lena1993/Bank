package com.bank.simulator.dto;

public class CardData {

    private String cardPan;
    private Integer cardBalance;

    public String getCardPan() {
        return cardPan;
    }

    public void setCardPan(String cardPan) {
        this.cardPan = cardPan;
    }

    public Integer getCardBalance() {
        return cardBalance;
    }

    public void setCardBalance(Integer cardBalance) {
        this.cardBalance = cardBalance;
    }
}
