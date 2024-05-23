package com.service;

import com.pojo.Funds;
import com.pojo.Group;
import com.pojo.User;

import java.util.List;
import java.util.Map;

public interface FundService {


    List<Funds>ShowPersonalFlow(String username);

    List<Funds> fundsQueryService(String name,String content,String username);

    Map<String, Double> GetGroupBalance();

    List<User> GetBalanceOfEachMemberInGroup(String theAdminName);

    Group GetGroupPublicFund(String theAdminName);

    String Allocatefunds(String theuser,int Useramount,int pubilicAmount,String type);

    List<Funds> ShowGroupFlow(String username);


}
