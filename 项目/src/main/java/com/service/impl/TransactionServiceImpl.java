package com.service.impl;

import com.dao.FundDAO;
import com.dao.GroupDAO;
import com.dao.TransactionDAO;
import com.dao.UserDAO;
import com.dao.impl.FundDAOimpl;
import com.dao.impl.GroupDAOimpl;
import com.dao.impl.TransactionDAOimpl;
import com.dao.impl.UserDAOImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pojo.Funds;
import com.pojo.Group;
import com.pojo.Transaction;
import com.pojo.User;
import com.service.TransactionService;
import com.util.JDBCUtilV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionServiceImpl implements TransactionService {
    TransactionDAO transactionDAO = new TransactionDAOimpl();
    UserDAO userDAO = new UserDAOImpl();
    FundDAO fundDAO = new FundDAOimpl();
    GroupDAO groupDAO = new GroupDAOimpl();
    private static final Cache<String, User> userCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(100, TimeUnit.MINUTES) //
            .build();
    private static final Cache<String, Group> GroupCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .build();
    Connection connection = null;
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    Lock lock=new ReentrantLock();
     static Object object=new Object();
    //发送付款请求
    @Override
    public String SendTransaction(String formattedTime, String username, String object, String password,
                                  String hashedPassword, String inTheNameOf, int number, User user) {
        List<Group> groups;  //所有的用户以及群组
        List<User> users;
        if (!GroupCache.asMap().isEmpty() && !userCache.asMap().isEmpty()) {
            // 缓存不为空，从缓存中获取数据
            groups = new ArrayList<>(GroupCache.asMap().values());
            users = new ArrayList<>(userCache.asMap().values());

        } else {
            // 缓存为空，查询数据库
            users = userDAO.selectAllUser();
            groups = groupDAO.selectAllForAdmin();
            for (Group group : groups) {
                GroupCache.put(group.getGroupname(), group); // 缓存查询结果
            }
            for (User user1 : users) {
                userCache.put(user1.getUsername(), user1); // 缓存查询结果
            }
        }
        //确认交易对象是否存在
        List<String> names = new ArrayList<>();
        for (User user1 : users) {
            names.add(user1.getUsername());
        }
        for (Group group1 : groups) {
            names.add(group1.getGroupname());
        }   //获取所有的交易对象以及交易群组


        for (String name : names) {
            if (name.equals(object)) {
                if (object.equals(user.getUsername())) {
                    return "发起对象不能是自己";
                }

                if (inTheNameOf.equals("个人")) {//事务保证发送和冻结资金同时发生
                    if(number>user.getPersonalfunds()){
                        return "个人余额不足";
                    }
                    synchronized(this) {
                        Transaction transaction = new Transaction(username, object, formattedTime, number, "已结算", null, "个人");
                        try {
                            connection = JDBCUtilV2.getConnection();
                            connection.setAutoCommit(false);
                            //操作
                            userDAO.FreezePersonalFund(username, number);
                            transactionDAO.SendTransaction(transaction);
                            //提交
                            connection.commit();
                        } catch (Exception e) {
                            try {
                                connection.rollback();
                                logger.error(e.getMessage());
                                return "发生错误，请重新发送";
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        } finally {
                            JDBCUtilV2.release();
                        }
                    }

                } else {  //以群组名义
                    String groupname = user.getGroupid();//得到群组
                    if (groupname == null || groupname.isEmpty()) {
                        return "用户不在群组之中";
                    }
                    int GroupFunds = user.getGroupfunds();
                    if (GroupFunds < number) {
                        return "用户在群组内资金不足";
                    }
                    synchronized(this) {
                        Transaction transaction = new Transaction(username, object, formattedTime, number, "已结算", groupname, "群组");
                        try {
                            connection = JDBCUtilV2.getConnection();
                            connection.setAutoCommit(false);
                            //操作
                            userDAO.FreezeGroupFund(username, number);
                            transactionDAO.SendTransaction(transaction);
                            //提交
                            connection.commit();
                        } catch (Exception e) {
                            try {
                                connection.rollback();
                                logger.error(e.getMessage());
                                return "发生错误，请重新发送";
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        } finally {
                            JDBCUtilV2.release();
                        }
                    }

                }
                return "支付请求已发送";
            }
        }
        return "交易对象不存在";
    }

    @Override
    public List<Transaction> getTransactionByUsername(String username, String type) {

        if (type.equals("pay")) {
            return transactionDAO.GetFuKuanTransactionS(username);
        } else {
            List<Transaction> transactions = transactionDAO.GetShouKuanTransactionS(username);
            for (Transaction transaction : transactions) {
                if (transaction.getNominal() != null && transaction.getNominal().equals("群组")) {
                    transaction.setPayer(transaction.getGroupname());
                }
            }
            return transactions;
        }
    }
    //用户接受收款
    @Override
    public String UserAcceptShouKuan(String thePayer, String theAmount, int TheAmount, String theTime, String therecipient, String TheNominal) {
        List<Transaction> transactions=transactionDAO.GetShouKuanTransactionS(therecipient);//未转换前的收款单子。
        synchronized(this) {
            try {
                connection = JDBCUtilV2.getConnection();
                connection.setAutoCommit(false);
                //操作

                if (TheNominal.equals("个人")) {
                    transactionDAO.GetShouKuanRecord(thePayer, theAmount, theTime, therecipient);
                    //写入收款方流水
                    Funds funds1 = new Funds(therecipient, thePayer, theTime, "收入", theAmount, "已收款", "个人", null);
                    //付款方的流水
                    Funds funds2 = new Funds(thePayer, therecipient, theTime, "支出", theAmount, "已收款", "个人", null);
                    fundDAO.ShouKuanFund(funds1);
                    fundDAO.ShouKuanFund(funds2);
                } else {
                    //名义是群组，付款人则是群组
                    //修改的订单
                    for (Transaction transaction : transactions) {//获得已群组名义支付的正在的payer
                        if (transaction.getAmount() == TheAmount && transaction.getTransaction_time().equals(theTime)) {
                            transactionDAO.GetShouKuanRecord(transaction.getPayer(), theAmount, theTime, therecipient);
                            //写入流水
                            Funds funds1 = new Funds(therecipient, thePayer, theTime, "收入", theAmount, "已收款", "个人", null);
                            //付款方的流水
                            Funds funds2 = new Funds(transaction.getPayer(), therecipient, theTime, "支出", theAmount, "已收款", "群组", transaction.getGroupname());
                            fundDAO.ShouKuanFund(funds1);
                            fundDAO.ShouKuanFund(funds2);
                        }
                    }
                }
                //提交
                connection.commit();
            } catch (Exception e) {
                try {
                    connection.rollback();
                    logger.error(e.getMessage());
                    return "发生错误，请重新操作";
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } finally {
                JDBCUtilV2.release();
            }
        }
        return "已收款";
    }
    //用户决绝收款
    @Override
    public String UserDenyShouKuan(String thePayer, String theAmount, String theTime) {
        try {
            connection= JDBCUtilV2.getConnection();
            connection.setAutoCommit(false);
            //操作
            transactionDAO.DenyShoukuanRecord(thePayer,theAmount,theTime,"拒绝收款");
            //提交
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
                logger.error(e.getMessage());
                return "发生错误，请重新操作";
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }finally {
            JDBCUtilV2.release();
        }
        return "已拒绝";
    }
    //处理用户被拒绝收款后的操作
    @Override
    public String HandlePayDeny(String thePayer, String theAmount, String theTime,String type) {
        if (type.equals("again")) {
            try {
                connection = JDBCUtilV2.getConnection();
                connection.setAutoCommit(false);
                //操作
                transactionDAO.DenyShoukuanRecord(thePayer, theAmount, theTime, "已结算");
                //提交
                connection.commit();
            } catch (Exception e) {
                try {
                    connection.rollback();
                    logger.error(e.getMessage());
                    return "发生错误，请重新操作";
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            } finally {
                JDBCUtilV2.release();
            }
            return "再次发送成功";
        }else{
            try {
                connection= JDBCUtilV2.getConnection();
                connection.setAutoCommit(false);
                //操作
                transactionDAO.DenyShoukuanRecord(thePayer,theAmount,theTime,"退款");
                transactionDAO.CancelFuKuanAgain(thePayer,theAmount);
                //提交
                connection.commit();
            } catch (Exception e) {
                try {
                    connection.rollback();
                    logger.error(e.getMessage());
                    return "发生错误，请重新操作";
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }finally {
                JDBCUtilV2.release();
            }
            return "已取消支付";
        }
    }

    @Override
    public List<Transaction> GetGroupShouKuanSituation(String id) {
        User user=userDAO.selectByname(id);
        String groupname= user.getGroupid();
        List<Transaction> transactions=transactionDAO.GetShouKuanTransactionS(groupname);
        for (Transaction transaction : transactions) {
            if(transaction.getNominal()!=null&&transaction.getNominal().equals("群组")){
                transaction.setPayer(transaction.getGroupname());
            }
        }
        return transactions;
    }

    @Override
    public String GroupShouKuanaccept(String thePayer, String theAmount, int TheAmount, String theTime, String therecipient, String TheNominal) {

        User user=userDAO.selectByname(therecipient);
        String groupname= user.getGroupid(); //真正的收款者，群名
        //如果以群组名义，payer是群组名，如果名义是个人，payer是人名，
        try {
            connection= JDBCUtilV2.getConnection();
            connection.setAutoCommit(false);
            //操作
            if(TheNominal.equals("个人")){
                transactionDAO.GetGroupShouKuanRecord(thePayer,theAmount,theTime,groupname);//入账，修改订单信息
                //写入收款方流水
                Funds funds1=new Funds(therecipient,thePayer,theTime,"收入",theAmount,"已收款","群组",groupname);
                fundDAO.ShouKuanFund(funds1);
                //写入付款方流水
                Funds funds2 = new Funds(thePayer,groupname, theTime, "支出", theAmount, "已收款", "个人",null);
                fundDAO.ShouKuanFund(funds2);
            }else{
                //写入收款方流水
                Funds funds1=new Funds(therecipient,thePayer,theTime,"收入",theAmount,"已收款","群组",groupname);
                fundDAO.ShouKuanFund(funds1);
                //写入付款方流水
                List<Transaction> transactions=transactionDAO.GetShouKuanTransactionS(groupname);//得到未转换前的群组
                for (Transaction transaction : transactions) {//获得已群组名义支付的正在的payer
                    if(transaction.getAmount()==TheAmount&&transaction.getTransaction_time().equals(theTime)){
                        transactionDAO.GetGroupShouKuanRecord(transaction.getPayer(),theAmount,theTime,groupname);//入账，修改订单信息
                        Funds funds2 = new Funds(transaction.getPayer(),groupname, theTime, "支出", theAmount, "已收款", "群组",transaction.getGroupname());
                        fundDAO.ShouKuanFund(funds2);
                    }}

            }
            //提交
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
                return "发生错误，请重新操作";
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }finally {
            JDBCUtilV2.release();
        }
        return  "已收款";
    }


}
