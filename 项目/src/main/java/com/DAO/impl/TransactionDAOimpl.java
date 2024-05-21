package com.dao.impl;

import com.dao.BaseDAO;
import com.dao.TransactionDAO;
import com.pojo.Transaction;

import java.util.List;

public class TransactionDAOimpl extends BaseDAO implements TransactionDAO {


    @Override
    public int SendTransaction(Transaction transcation) {
        try {
            String sql = "INSERT INTO transactionrecords(Payer,recipient,transaction_time,amount,status,nominal,groupname) VALUES (?,?,?,?,?,?,?)";
            return executeUpdate(sql,transcation.getPayer(),transcation.getRecipient(),transcation.getTransaction_time(),transcation.getAmount(),
                    transcation.getStatus(),transcation.getNominal(),transcation.getGroupname());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Transaction> GetFuKuanTransactionS(String id) {
        try {
            String sql = "SELECT recipient,amount,transaction_time,status FROM transactionrecords WHERE Payer=?";
            return executeQuery(Transaction.class, sql, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Transaction> GetShouKuanTransactionS(String id) {
        try {
            String sql = "SELECT Payer,recipient,amount,transaction_time,nominal,groupname FROM transactionrecords WHERE recipient=? AND status=?";
            return executeQuery(Transaction.class, sql, id,"已结算");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int GetShouKuanRecord(String payer, String amount, String time,String recipient) {
        try {
            String sql2 ="UPDATE transactionrecords SET status=? where Payer = ? and transaction_time=? and amount=? ";
            //修改交易状态
            int i=executeUpdate(sql2,"已收款",payer,time,amount);
            //收款方入账
            String sql3 ="UPDATE tb_user SET Personalfunds=Personalfunds+? where username=? ";
            int b=executeUpdate(sql3,amount,recipient);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public int DenyShoukuanRecord(String payer, String amount, String time,String status) {
        try {
            String sql2 ="UPDATE transactionrecords SET status=? where Payer = ? and transaction_time=? and amount=? ";
            return executeUpdate(sql2,status,payer,time,amount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int CancelFuKuanAgain(String payer, String amount) {
        try {
            String sql3 ="UPDATE tb_user SET Personalfunds=Personalfunds+? where username=? ";
            int b=executeUpdate(sql3,amount,payer);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int DeleteRecord(String id, String time, String recipient) {
        try {
            String sql ="DELETE FROM transactionrecords WHERE Payer = ?AND recipient=? AND transaction_time=?";
            return executeUpdate(sql,id,recipient,time);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public int GetGroupShouKuanRecord(String payer, String amount, String time,String recipient) {
        try {
            String sql2 ="UPDATE transactionrecords SET status=? where Payer = ? and transaction_time=? and amount=? ";
            //修改交易状态
            int i=executeUpdate(sql2,"已收款",payer,time,amount);
            //收款方入账
            String sql3 ="UPDATE tb_group SET publicfunds=publicfunds+? where groupname=? ";
            int b=executeUpdate(sql3,amount,recipient);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
