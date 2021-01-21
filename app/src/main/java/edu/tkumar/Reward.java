package edu.tkumar;

import java.io.Serializable;

public class Reward implements Serializable {

    String givenName;
    String amount;
    String note;
    String awardDate;

    public Reward(String givenName, String amount, String note, String awardDate) {
        this.givenName = givenName;
        this.amount = amount;
        this.note = note;
        this.awardDate = awardDate;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAwardDate(String awardDate) {
        this.awardDate = awardDate;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getAwardDate() {
        return awardDate;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "givenName='" + givenName + '\'' +
                ", amount='" + amount + '\'' +
                ", note='" + note + '\'' +
                ", awardDate='" + awardDate + '\'' +
                '}';
    }
}
