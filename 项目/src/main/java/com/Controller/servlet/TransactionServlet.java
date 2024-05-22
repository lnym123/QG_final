package com.controller.servlet;

import com.alibaba.fastjson.JSONArray;
import com.controller.BaseServlet;
import com.dao.FundDAO;
import com.dao.GroupDAO;
import com.dao.TransactionDAO;
import com.dao.UserDAO;
import com.dao.impl.FundDAOimpl;
import com.dao.impl.GroupDAOimpl;
import com.dao.impl.TransactionDAOimpl;
import com.dao.impl.UserDAOImpl;
import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pojo.Funds;
import com.pojo.Group;
import com.pojo.Transaction;
import com.pojo.User;
import com.util.JDBCUtilV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.controller.servlet.UserServlet.hashPasswordSHA256;


@WebServlet("/transaction/*")
public class TransactionServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServlet.class);
    TransactionDAO transactionDAO = new TransactionDAOimpl();
    UserDAO userDAO = new UserDAOImpl();
    FundDAO fundDAO= new FundDAOimpl();
    GroupDAO groupDAO= new GroupDAOimpl();
    private static final Cache<String, User> userCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES) //
            .build();
    private static final Cache<String, Group> GroupCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();


    public void SendTransaction(HttpServletRequest request, HttpServletResponse resp) throws IOException, NoSuchAlgorithmException {
        LocalDateTime currentTime = LocalDateTime.now(); // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);  // 将当前时间格式化为字符串
        resp.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");//发起者
        String object = request.getParameter("object");//收款者
        String password = request.getParameter("password");//密码
        String hashedPassword = hashPasswordSHA256(password);

        int number = Integer.parseInt(request.getParameter("number"));//交易金额
        if (number<1) {
            resp.getWriter().write("请输入大于0的数字");
            return;
        }
        User user = userDAO.selectByname(username);
        String TruePassword = user.getPassword();  //得到正确密码
        if (!TruePassword.equals(hashedPassword )) {
            resp.getWriter().write("密码错误");
            return;
        }
        List<Group> groups;  //所有的用户以及群组
        List<User> users;
        if (!GroupCache.asMap().isEmpty()&&!userCache.asMap().isEmpty()) {
            // 缓存不为空，从缓存中获取数据
            groups = new ArrayList<>(GroupCache.asMap().values());
            users = new ArrayList<>(userCache.asMap().values());

        }else {
//            // 缓存为空，查询数据库
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
            if(name.equals(object)){
                String inTheNameOf = request.getParameter("InTheNameOf");
                if(inTheNameOf.equals("个人")){//事务保证发送和冻结资金同时发生
                    Transaction transaction = new Transaction(username,object,formattedTime,number,"已结算",null,"个人");
                    Connection connection= null;
                    try {
                        connection= JDBCUtilV2.getConnection();
                        connection.setAutoCommit(false);
                        //操作
                        userDAO.FreezePersonalFund(username,number);
                        transactionDAO.SendTransaction(transaction);
                        //提交
                        connection.commit();
                    } catch (Exception e) {
                        try {
                            connection.rollback();
                            logger.error(e.getMessage());
                            resp.getWriter().write("发生错误，请重新发送");
                            return;
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }finally {
                        JDBCUtilV2.release();
                    }


                }else{  //以群组名义
                    String groupname = user.getGroupid();//得到群组
                    if (groupname == null || groupname.isEmpty()) {
                        resp.getWriter().write("用户不在群组之中");
                        return;
                    }
                    int GroupFunds = user.getGroupfunds();
                    if (GroupFunds < number) {
                        resp.getWriter().write("用户在群组内资金不足");
                        return;
                    }
                    Transaction transaction = new Transaction(username,object,formattedTime,number,"已结算",groupname,"群组");
                    Connection connection= null;
                    try {
                        connection= JDBCUtilV2.getConnection();
                        connection.setAutoCommit(false);
                        //操作
                        userDAO.FreezeGroupFund(username,number);
                        transactionDAO.SendTransaction(transaction);
                        //提交
                        connection.commit();
                    } catch (Exception e) {
                        try {
                            connection.rollback();
                            logger.error(e.getMessage());
                            resp.getWriter().write("发生错误，请重新发送");
                            return;
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }finally {
                        JDBCUtilV2.release();
                    }

                }
                resp.getWriter().write("支付请求已发出");
                return;
            }
        }
        resp.getWriter().write("交易对象不存在");

    }


    public void  GetFuKuanSituation(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        String id = request.getParameter("id");
        List<Transaction> transactions=transactionDAO.GetFuKuanTransactionS(id);
        String jsonString= JSON.toJSONString(transactions);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }
    public void GetShouKuanSituation(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String id = request.getParameter("id");
        List<Transaction> transactions=transactionDAO.GetShouKuanTransactionS(id);
        for (Transaction transaction : transactions) {
            if(transaction.getNominal()!=null&&transaction.getNominal().equals("群组")){
                transaction.setPayer(transaction.getGroupname());
            }
        }
        String jsonString= JSON.toJSONString(transactions);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }
    public void UserAcceptShouKuan(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        String thePayer = request.getParameter("ThePayer");   //名义是群组，则为群组名
        System.out.println("付款人"+thePayer);
        String theAmount = request.getParameter("TheAmount");
        int TheAmount=Integer.parseInt(theAmount);
        String theTime = request.getParameter("TheTime");
        String therecipient = request.getParameter("Therecipient"); //为user本人
        String TheNominal=request.getParameter("TheNominal");
        List<Transaction> transactions=transactionDAO.GetShouKuanTransactionS(therecipient);//未转换前的收款单子。
        Connection connection= null;
        try {
            connection = JDBCUtilV2.getConnection();
            connection.setAutoCommit(false);
            //操作

        if(TheNominal.equals("个人")) {
            transactionDAO.GetShouKuanRecord(thePayer,theAmount,theTime,therecipient);
            //写入收款方流水
            Funds funds1 = new Funds(therecipient, thePayer, theTime, "收入", theAmount, "已收款", "个人", null);
            //付款方的流水
            Funds funds2 = new Funds(thePayer, therecipient, theTime, "支出", theAmount, "已收款", "个人", null);
            fundDAO.ShouKuanFund(funds1);
            fundDAO.ShouKuanFund(funds2);
        }else{
            //名义是群组，付款人则是群组
            //修改的订单
            for (Transaction transaction : transactions) {//获得已群组名义支付的正在的payer
                if(transaction.getAmount()==TheAmount&&transaction.getTransaction_time().equals(theTime)){
                    transactionDAO.GetShouKuanRecord(transaction.getPayer(), theAmount,theTime,therecipient);
                    //写入流水
                    Funds funds1=new Funds(therecipient,thePayer,theTime,"收入",theAmount,"已收款","个人",null);
                    //付款方的流水
                    Funds funds2=new Funds(transaction.getPayer(),therecipient,theTime,"支出",theAmount,"已收款","群组",transaction.getGroupname());
                    fundDAO.ShouKuanFund(funds1);
                    fundDAO.ShouKuanFund(funds2);
                }}
        }
            //提交
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
                logger.error(e.getMessage());
                resp.getWriter().write("发生错误，请重新操作");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }finally {
            JDBCUtilV2.release();
        }

        resp.getWriter().write("已收款");
    }
    public void UserDenyShouKuan(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        String thePayer = request.getParameter("ThePayer");
        String theAmount = request.getParameter("TheAmount");
        String theTime = request.getParameter("TheTime");
        String therecipient = request.getParameter("Therecipient");
        Connection connection= null;
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
                resp.getWriter().write("发生错误，请重新操作");
                return;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }finally {
            JDBCUtilV2.release();
        }
        resp.getWriter().write("已拒绝");
    }
    public void SendFuKuanAgain(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        String thePayer = request.getParameter("ThePayer");
        String theAmount = request.getParameter("TheAmount");
        String theTime = request.getParameter("TheTime");
        Connection connection= null;
        try {
            connection= JDBCUtilV2.getConnection();
            connection.setAutoCommit(false);
            //操作
            transactionDAO.DenyShoukuanRecord(thePayer,theAmount,theTime,"已结算");
            //提交
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
                logger.error(e.getMessage());
                resp.getWriter().write("发生错误，请重新操作");
                return;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }finally {
            JDBCUtilV2.release();
        }

        resp.getWriter().write("再次发送成功");
    }

    public void CancelFuKuanAgain(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String thePayer = request.getParameter("ThePayer");
        String theAmount = request.getParameter("TheAmount");
        String theTime = request.getParameter("TheTime");
        Connection connection= null;
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
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }finally {
            JDBCUtilV2.release();
        }

        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已取消支付");

    }
   public  void DeleteFuKuan(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
       String thePayer = request.getParameter("ThePayer");
       String theTime = request.getParameter("TheTime");
       String therecipient = request.getParameter("Therecipient");
       transactionDAO.DeleteRecord(thePayer,theTime,therecipient);
       resp.setCharacterEncoding("UTF-8");
       resp.getWriter().write("已删除");

   }
   public void  GetGroupShouKuanSituation(HttpServletRequest request, HttpServletResponse resp) throws IOException {
       String id = request.getParameter("id");
       User user=userDAO.selectByname(id);
       String groupname= user.getGroupid();
       List<Transaction> transactions=transactionDAO.GetShouKuanTransactionS(groupname);
       for (Transaction transaction : transactions) {
           if(transaction.getNominal()!=null&&transaction.getNominal().equals("群组")){
               transaction.setPayer(transaction.getGroupname());
           }
       }
       String jsonString= JSON.toJSONString(transactions);
       resp.setContentType("text/json;charset=utf-8");
       resp.getWriter().write(jsonString);
   }

   public void GroupShouKuanaccept(HttpServletRequest request, HttpServletResponse resp) throws IOException {
       resp.setCharacterEncoding("UTF-8");
       String thePayer = request.getParameter("ThePayer");
       String theAmount = request.getParameter("TheAmount");
       int TheAmount=Integer.parseInt(theAmount);
       String theTime = request.getParameter("TheTime");
       String therecipient = request.getParameter("Therecipient"); //收款对象是群，传过来的是群管理员名字
       User user=userDAO.selectByname(therecipient);
       String groupname= user.getGroupid(); //真正的收款者，群名
       String TheNominal=request.getParameter("TheNominal");
       //如果以群组名义，payer是群组名，如果名义是个人，payer是人名，
       Connection connection= null;
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
               resp.getWriter().write("发生错误，请重新操作");
               return;
           } catch (Exception ex) {
               throw new RuntimeException(ex);
           }
       }finally {
           JDBCUtilV2.release();
       }


       resp.getWriter().write("已收款");
   }
}



