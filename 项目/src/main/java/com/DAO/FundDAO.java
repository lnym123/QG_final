package com.dao;

import com.pojo.Funds;

import java.util.List;

public interface FundDAO {

    List<Funds> SelectPersonalFunds(String username);

    List<Funds> selectByCondition(String username,String name,String content);

    int Allocatefunds(String id,int amount1,int amount2,String groupid);

    List<Funds> SelectGroupFunds(String groupid);

    int ShouKuanFund(Funds funds);
}
