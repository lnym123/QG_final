package com.controller.servlet;
import com.controller.BaseServlet;
import com.alibaba.fastjson.JSON;
import com.pojo.Group;
import com.service.GroupService;
import com.service.impl.GroupServiceImpl;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/group/*")
public class GroupServlet extends BaseServlet {
    GroupService groupservice=new GroupServiceImpl();
   //进入主页面展示公开的企业群组
    public void  ShowGroups(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        List<Group> groups=groupservice.GetAllGroups("notAll");
        String jsonString = JSON.toJSONString(groups);
        resp.getWriter().write(jsonString);
    }

   //条件查询群组
    public void GroupQueryService(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String groupName = req.getParameter("groupName");
        String direction = req.getParameter("direction");
        List<Group> groups;
        groups=groupservice.GroupQuery(groupName,direction);
        String jsonString= JSON.toJSONString(groups);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }

    public void ShowPersonalGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json;charset=utf-8");
        String username = req.getParameter("username");
        Group group=groupservice.ShowPersonalGroup(username);
        String jsonString= JSON.toJSONString(group);
        resp.getWriter().write(jsonString);

    }
    //企业管理员修改企业信息
    public void ForAdminChangeGroup (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        String id = req.getParameter("id");
        String scale = req.getParameter("scale");
        String direction = req.getParameter("direction");
        String number = req.getParameter("number");
        String visiable=req.getParameter("visiable");
        int TrueNum = Integer.parseInt(number);
        if(TrueNum<1){
            resp.getWriter().write("人数错误");
            return;
        }
        else if (!ValidationHelper.isValidLocation(scale)) {
                resp.getWriter().write("规模格式错误");
                return;
            }
        else if (!ValidationHelper.isValidLocation(direction)) {
            resp.getWriter().write("企业方向格式错误");
            return;
        }
        groupservice.ChangeGroupInfo(id,number,scale,direction,visiable);
        resp.getWriter().write("修改完毕");
    }
    public void selectAllGroupForAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Group> groups=groupservice.GetAllGroups("all");
        String jsonString= JSON.toJSONString(groups);
        resp.setContentType("text/json;charset=utf-8");
        resp.getWriter().write(jsonString);
    }
   //管理员封禁以及解封群组
    public void ForAdminBanGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        groupservice.AdminOperateGroup(id,"ban");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已封禁");
    }

    public void ForAdminUnBanGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        groupservice.AdminOperateGroup(id,"unban");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已解封");
    }

    //退出群组
    public void LogOutGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String theAdmin = req.getParameter("TheAdmin");
        int PublicFunds = Integer.parseInt(req.getParameter("PublicFunds"));
        groupservice.LogOutGroup(theAdmin,PublicFunds);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("已注销");
    }
}
