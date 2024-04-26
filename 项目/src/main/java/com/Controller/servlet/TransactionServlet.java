package com.Controller.servlet;

import com.Controller.BaseServlet;
import com.DAO.FundDAO;
import com.DAO.TransactionDAO;
import com.DAO.UserDAO;
import com.DAO.impl.FundDAOimpl;
import com.DAO.impl.TransactionDAOimpl;
import com.DAO.impl.UserDAOImpl;
import com.alibaba.fastjson.JSON;
import com.pojo.Funds;
import com.pojo.Transaction;
import com.pojo.User;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@WebServlet("/transaction/*")
public class TransactionServlet extends BaseServlet {
    TransactionDAO transactionDAO = new TransactionDAOimpl();
    UserDAO userDAO = new UserDAOImpl();
    FundDAO fundDAO= new FundDAOimpl();

    public void SendTransaction(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        LocalDateTime currentTime = LocalDateTime.now(); // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);  // 将当前时间格式化为字符串
        resp.setCharacterEncoding("UTF-8");
        String upload = null;   //是否上传
        String username = request.getParameter("username");//发起者
        String object = request.getParameter("object");//收款者
        String password = request.getParameter("password");//密码
        int number = Integer.parseInt(request.getParameter("number"));//交易金额
        User user = userDAO.selectByname(username);
        String TruePassword = user.getPassword();  //得到正确密码

        if (!TruePassword.equals(password)) {
            resp.getWriter().write("密码错误");
            return;
        }

        String inTheNameOf = request.getParameter("InTheNameOf");
        if(inTheNameOf.equals("个人")){
        Transaction transaction = new Transaction(username,object,formattedTime,number,"已结算",null,"个人");
        userDAO.FreezePersonalFund(username,number);
        transactionDAO.SendTransaction(transaction);
        }else{  //以群组名义
            String groupname = user.getGroupid();//得到群组
            if (groupname == null || groupname.isEmpty()) {
                 resp.getWriter().write("用户不在群组之中");
                 return;
            }
            upload = request.getParameter("upload");
            int GroupFunds = user.getGroupfunds();
            if (GroupFunds < number) {
              resp.getWriter().write("用户在群组内资金不足");
                return;
                         }
            Transaction transaction = new Transaction(username,object,formattedTime,number,"已结算",groupname,"群组");
            userDAO.FreezeGroupFund(username,number);
            transactionDAO.SendTransaction(transaction);
        }
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("支付请求已发出");
    }


    public void  GetFuKuanSituation(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
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
        String thePayer = request.getParameter("ThePayer");
        String theAmount = request.getParameter("TheAmount");
        String theTime = request.getParameter("TheTime");
        String therecipient = request.getParameter("Therecipient");
        transactionDAO.GetShouKuanRecord(thePayer,theAmount,theTime,therecipient);
        //写入流水
        Funds funds1=new Funds(therecipient,thePayer,theTime,"收入",theAmount,"已收款","个人",null);
        //付款方的流水
        Funds funds2=new Funds(thePayer,therecipient,theTime,"支出",theAmount,"已收款","个人",null);
        fundDAO.ShouKuanFund(funds1);
        fundDAO.ShouKuanFund(funds2);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已收款");
    }
    public void UserDenyShouKuan(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String thePayer = request.getParameter("ThePayer");
        String theAmount = request.getParameter("TheAmount");
        String theTime = request.getParameter("TheTime");
        String therecipient = request.getParameter("Therecipient");
        transactionDAO.DenyShoukuanRecord(thePayer,theAmount,theTime,"拒绝收款");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已拒绝");
    }
    public void SendFuKuanAgain(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String thePayer = request.getParameter("ThePayer");
        String theAmount = request.getParameter("TheAmount");
        String theTime = request.getParameter("TheTime");
        String therecipient = request.getParameter("Therecipient");
        transactionDAO.DenyShoukuanRecord(thePayer,theAmount,theTime,"已结算");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("再次发送成功");
    }

    public void CancelFuKuanAgain(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String thePayer = request.getParameter("ThePayer");
        String theAmount = request.getParameter("TheAmount");
        String theTime = request.getParameter("TheTime");
        String therecipient = request.getParameter("Therecipient");
        transactionDAO.DenyShoukuanRecord(thePayer,theAmount,theTime,"退款");
        transactionDAO.CancelFuKuanAgain(thePayer,theAmount);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已取消支付");

    }
   public  void DeleteFuKuan(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
       String thePayer = request.getParameter("ThePayer");
       String theAmount = request.getParameter("TheAmount");
       String theTime = request.getParameter("TheTime");
       String therecipient = request.getParameter("Therecipient");
       transactionDAO.DeleteRecord(thePayer,theTime,therecipient);
       resp.setCharacterEncoding("UTF-8");
       resp.getWriter().write("已删除");

   }
       public void  GetGroupShouKuanSituation(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
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

   public void GroupShouKuanaccept(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
       String thePayer = request.getParameter("ThePayer");
       String theAmount = request.getParameter("TheAmount");
       int TheAmount=Integer.parseInt(theAmount);
       String theTime = request.getParameter("TheTime");
       String therecipient = request.getParameter("Therecipient"); //收款对象是群，传过来的是群管理员名字
       User user=userDAO.selectByname(therecipient);
       String groupname= user.getGroupid(); //真正的收款者，群名
       String TheNominal=request.getParameter("TheNominal");
       //如果以群组名义，payer是群组名，如果名义是个人，payer是人名，
       transactionDAO.GetGroupShouKuanRecord(thePayer,theAmount,theTime,groupname);//入账，修改订单信息
       if(TheNominal.equals("个人")){
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
                 Funds funds2 = new Funds(transaction.getPayer(),groupname, theTime, "支出", theAmount, "已收款", "群组",transaction.getGroupname());
                 fundDAO.ShouKuanFund(funds2);
        }}

       }
       resp.setCharacterEncoding("UTF-8");
       resp.getWriter().write("已收款");
   }
}

//

//



////付款方的流水
////               if(transaction.getNominal().equals("群组")) {
////
////               }else{
////                   Funds funds2 = new Funds(transaction.getPayer(), groupname, theTime, "支出", theAmount, "已收款", "个人", transaction.getGroupname());
////                   fundDAO.ShouKuanFund(funds2);
////               }
//
//
//           }
//                   }
//


