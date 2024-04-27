package com.pojo;

public class Transaction {
    String Payer;
    String recipient;
    String transaction_time;
    int amount;
    String status;
    String nominal;
    String groupname;

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getTransaction_time() {
        return transaction_time;
    }

    public void setTransaction_time(String transaction_time) {
        this.transaction_time = transaction_time;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public Transaction() {
    }


    public Transaction(String payer, String recipient, String transaction_time, int amount, String status, String groupname, String nominal) {
        Payer = payer;
        this.recipient = recipient;
        this.transaction_time = transaction_time;
        this.amount = amount;
        this.status = status;
        this.groupname = groupname;
        this.nominal = nominal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }



    public String getPayer() {
        return Payer;
    }

    public void setPayer(String payer) {
        Payer = payer;
    }


}
