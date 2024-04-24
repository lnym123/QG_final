package com.DAO.impl;

import com.DAO.BaseDAO;
import com.DAO.FundDAO;
import com.pojo.Funds;

import java.util.List;


public class FundDAOimpl extends BaseDAO implements FundDAO {
    @Override
    public List<Funds> SelectPersonalFunds(String username) {
        try {
                String sql = "SELECT transaction_object,mode,transaction_time,amount,transaction_status,type FROM tb_funds WHERE username=? ";
            return executeQuery(Funds.class, sql, username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Funds> selectByCondition(String username,String name, String content) {
        try {
            String sql = "SELECT transaction_object,mode,transaction_time, amount, transaction_status, type FROM tb_funds WHERE username=? AND " + name + " = ?";
            return executeQuery(Funds.class, sql, username,content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int Allocatefunds(String id, int amount1,int amount2,String groupid) {
        try {
            String sql1 = "UPDATE tb_user SET Groupfunds=? WHERE username = ?";
            String sql2 = "UPDATE tb_group SET publicfunds=? WHERE groupname = ?";
            return executeUpdate(sql1,amount1,id)+executeUpdate(sql2,amount2,groupid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Funds> SelectGroupFunds(String groupid) {
        try {
            String sql = "SELECT username,transaction_object,transaction_time,amount,transaction_status,type FROM tb_funds WHERE groupname=? AND mode=? ";
            return executeQuery(Funds.class, sql,groupid,"群组");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
