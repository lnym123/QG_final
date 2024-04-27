package com.DAO;

import com.pojo.Transaction;

import java.util.List;

public interface TransactionDAO {

    int SendTransaction(Transaction transcation);
    List<Transaction> GetFuKuanTransactionS(String id);

    List<Transaction> GetShouKuanTransactionS(String id);

    int GetShouKuanRecord(String payer,String amount,String time,String recipient);

    int DenyShoukuanRecord(String payer, String amount, String time,String status);

    int CancelFuKuanAgain(String payer, String amount);

    int DeleteRecord(String id,String time,String recipient);

    int GetGroupShouKuanRecord(String payer, String amount, String time,String recipient);
}
