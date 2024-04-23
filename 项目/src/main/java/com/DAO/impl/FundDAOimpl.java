package com.DAO.impl;

import com.DAO.BaseDAO;
import com.DAO.FundDAO;
import com.pojo.Funds;

import java.util.List;


public class FundDAOimpl extends BaseDAO implements FundDAO {
    @Override
    public List<Funds> SelectPersonalFunds(String username) {
        try {
                String sql = "SELECT transaction_object,transaction_time,amount,transaction_status,type FROM tb_funds WHERE username=? ";
            return executeQuery(Funds.class, sql, username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Funds> selectByCondition(String username,String name, String content) {
        try {
            String sql = "SELECT transaction_object, transaction_time, amount, transaction_status, type FROM tb_funds WHERE username=? AND " + name + " = ?";
            return executeQuery(Funds.class, sql, username,content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
