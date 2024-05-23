package com.service;

import com.pojo.Transaction;
import com.pojo.User;

import java.util.List;

public interface TransactionService {
    String SendTransaction(String formattedTime, String username, String object , String password,
                           String hashedPassword, String inTheNameOf, int number, User user);

    List<Transaction> getTransactionByUsername(String username,String type);

    String UserAcceptShouKuan(String thePayer,String theAmount,int TheAmount,String theTime,String therecipient,String TheNominal);

    String UserDenyShouKuan(String thePayer,String theAmount,String theTime);

    String HandlePayDeny(String thePayer,String theAmount,String theTime,String type);

    List<Transaction> GetGroupShouKuanSituation(String id);

    String GroupShouKuanaccept(String thePayer,String theAmount,int TheAmount,String theTime,String therecipient,String TheNominal);

}
