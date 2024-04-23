package com.DAO;

import com.pojo.Funds;

import java.util.List;

public interface FundDAO {

    List<Funds> SelectPersonalFunds(String username);

    List<Funds> selectByCondition(String username,String name,String content);
}
