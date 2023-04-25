package com.bank.simulator.model;

public class Card {
    private String pin;
    private String pan;
    private String expiryDate;
    private String cardHolder;
    private String cardType;
    private String issuer;

    public Card(String pin, String pan, String expiryDate, String cardHolder, String cardType, String issuer) {
        this.pin = pin;
        this.pan = pan;
        this.expiryDate = expiryDate;
        this.cardHolder = cardHolder;
        this.cardType = cardType;
        this.issuer = issuer;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
