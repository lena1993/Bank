package com.bank.simulator.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cards")
public class Cards implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(name = "pin")
    private String pin;

    @Column(name = "pan")
    private String pan;

    @Column(name = "expiryDate")
    private String expiryDate;

    @Column(name = "cardHolder")
    private String cardHolder;

    @Column(name = "cardType")
    private String cardType;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "balance")
    private Integer balance;

    public Cards(){}

    public Cards(String pin, String pan, String expiryDate, String cardHolder, String cardType, String issuer, Integer balance) {
        this.pin = pin;
        this.pan = pan;
        this.expiryDate = expiryDate;
        this.cardHolder = cardHolder;
        this.cardType = cardType;
        this.issuer = issuer;
        this.balance = balance;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
