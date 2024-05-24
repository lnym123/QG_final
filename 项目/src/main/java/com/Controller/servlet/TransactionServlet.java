package com.controller.servlet;

import com.controller.BaseServlet;

import com.dao.TransactionDAO;
import com.dao.UserDAO;

import com.dao.impl.TransactionDAOimpl;
import com.dao.impl.UserDAOImpl;
import com.alibaba.fastjson.JSON;

import com.pojo.Transaction;
import com.pojo.User;
import com.service.TransactionService;
import com.service.impl.TransactionServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


import static com.controller.servlet.UserServlet.hashPasswordSHA256;


@WebServlet("/transaction/*")
public class TransactionServlet extends BaseServlet {
    TransactionService transactionService = new TransactionServiceImpl();
    TransactionDAO transactionDAO = new TransactionDAOimpl();
    UserDAO userDAO = new UserDAOImpl();
    //发送付款请求
    public void SendTransaction(HttpServletRequest request, HttpServletResponse resp) throws IOException, NoSuchAlgorithmException {
        LocalDateTime currentTime = LocalDateTime.now(); // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);  // 将当前时间格式化为字符串
        resp.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");//发起者
        String object = request.getParameter("object");//收款者
        String password = request.getParameter("password");//密码
        String hashedPassword = hashPasswordSHA256(password);
        String inTheNameOf = request.getParameter("InTheNameOf");
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

        //处理发送请求，得到结果
        String result=transactionService.SendTransaction(formattedTime,username,object,password,hashedPassword,inTheNameOf,number,user);
        resp.getWriter().write(result);

        }


    //得到个人的付款情况
    public void  GetFuKuanSituation(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        String id = request.getParameter("id");
        List<Transaction> transactions=transactionService.getTransactionByUsername(id,"pay");
        String jsonString= JSON.toJSONString(transactions);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }
    //得到个人的收款情况
    public void GetShouKuanSituation(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        String id = request.getParameter("id");
        List<Transaction> transactions=transactionService.getTransactionByUsername(id,"get");
        String jsonString= JSON.toJSONString(transactions);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }
    //用户接受付款
    public void UserAcceptShouKuan(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        String thePayer = request.getParameter("ThePayer");   //名义是群组，则为群组名
        String theAmount = request.getParameter("TheAmount");
        int TheAmount=Integer.parseInt(theAmount);
        String theTime = request.getParameter("TheTime");
        String therecipient = request.getParameter("Therecipient"); //为user本人
        String TheNominal=request.getParameter("TheNominal");
        String result=transactionService.UserAcceptShouKuan(thePayer,theAmount,TheAmount,theTime,therecipient,TheNominal);
        resp.getWriter().write(result);
    }
    //用户拒绝付款
    public void UserDenyShouKuan(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        String thePayer = request.getParameter("ThePayer");
        String theAmount = request.getParameter("TheAmount");
        String theTime = request.getParameter("TheTime");
        String result=transactionService.UserDenyShouKuan(thePayer,theAmount,theTime);
        resp.getWriter().write(result);
    }
    //用户被拒绝收款后再次发送请求
    public void SendFuKuanAgain(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        String thePayer = request.getParameter("ThePayer");
        String theAmount = request.getParameter("TheAmount");
        String theTime = request.getParameter("TheTime");
        String result=transactionService.HandlePayDeny(thePayer,theAmount,theTime,"again");
        resp.getWriter().write(result);
    }
    //用户被拒绝后取消付款
    public void CancelFuKuanAgain(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        String thePayer = request.getParameter("ThePayer");
        String theAmount = request.getParameter("TheAmount");
        String theTime = request.getParameter("TheTime");
        String result=transactionService.HandlePayDeny(thePayer,theAmount,theTime,"cancel");
        resp.getWriter().write(result);

    }
    //删除付款记录
   public  void DeleteFuKuan(HttpServletRequest request, HttpServletResponse resp) throws IOException {
       String thePayer = request.getParameter("ThePayer");
       String theTime = request.getParameter("TheTime");
       String therecipient = request.getParameter("Therecipient");
       transactionDAO.DeleteRecord(thePayer,theTime,therecipient);
       resp.setCharacterEncoding("UTF-8");
       resp.getWriter().write("已删除");

   }
    //得到企业的收款情况
   public void  GetGroupShouKuanSituation(HttpServletRequest request, HttpServletResponse resp) throws IOException {
       String id = request.getParameter("id");
       List<Transaction> transactions=transactionService.GetGroupShouKuanSituation(id);
       String jsonString= JSON.toJSONString(transactions);
       resp.setContentType("text/json;charset=utf-8");
       resp.getWriter().write(jsonString);
   }
   //群组接受付款
   public void GroupShouKuanaccept(HttpServletRequest request, HttpServletResponse resp) throws IOException {
       resp.setCharacterEncoding("UTF-8");
       String thePayer = request.getParameter("ThePayer");
       String theAmount = request.getParameter("TheAmount");
       int TheAmount=Integer.parseInt(theAmount);
       String theTime = request.getParameter("TheTime");
       String therecipient = request.getParameter("Therecipient");
       String TheNominal=request.getParameter("TheNominal");//收款对象是群，传过来的是群管理员名字
       String result=transactionService.GroupShouKuanaccept(thePayer,theAmount,TheAmount,theTime,therecipient,TheNominal);
       resp.getWriter().write(result);
   }
}



