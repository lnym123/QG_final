package com.pojo;

public class Funds {

    String username;
    String transaction_object;
    String transaction_time;
    String type;
    String amount;
    String transaction_status;


    @Override
    public String toString() {
        return "Funds{" +
                "username='" + username + '\'' +
                ", transaction_object='" + transaction_object + '\'' +
                ", transaction_time='" + transaction_time + '\'' +
                ", type='" + type + '\'' +
                ", amount='" + amount + '\'' +
                ", transaction_status='" + transaction_status + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTransaction_object() {
        return transaction_object;
    }

    public void setTransaction_object(String transaction_object) {
        this.transaction_object = transaction_object;
    }

    public String getTransaction_time() {
        return transaction_time;
    }

    public void setTransaction_time(String transaction_time) {
        this.transaction_time = transaction_time;
    }

    public String gettype() {
        return type;
    }

    public void settype(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransaction_status() {
        return transaction_status;
    }

    public void setTransaction_status(String transaction_status) {
        this.transaction_status = transaction_status;
    }
}
