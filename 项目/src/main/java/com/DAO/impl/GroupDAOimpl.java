package com.dao.impl;

import com.dao.BaseDAO;
import com.dao.GroupDAO;
import com.alibaba.druid.util.StringUtils;
import com.pojo.Group;
import com.util.JDBCUtilV2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupDAOimpl extends BaseDAO implements GroupDAO {
    @Override
    public List<Group> selectAll() {
        try {
            String sql = "SELECT groupname,number,scale,direction,publicfunds FROM tb_group WHERE visiable = ?";
            return executeQuery(Group.class, sql, "true");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Group> selectBycondition(String groupName, String direction) throws SQLException {
        String SQL_SELECT_GROUP = "SELECT groupname,number,direction,scale FROM tb_group WHERE 1=1 %s";
        List<String> conditions = new ArrayList<>();

        // 根据groupName添加模糊查询条件
        if (!StringUtils.isEmpty(groupName)) {
            conditions.add("AND groupname LIKE ?");
        } else {
            // 如果groupName为空，添加一个始终为真的条件以保持WHERE子句的完整性
            conditions.add("AND 1=1");
        }

        // 根据direction添加条件，这里也假设可能需要模糊查询，所以使用LIKE
        if (!StringUtils.isEmpty(direction)) {
            conditions.add("AND direction LIKE ?");
        } else {
            conditions.add("AND 1=1");
        }

        // 拼接查询条件
        String whereClause = conditions.stream().collect(Collectors.joining(" "));

        // 构建最终SQL语句
        String sql = String.format(SQL_SELECT_GROUP, whereClause);

        try (Connection connection = JDBCUtilV2.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int paramIndex = 1;
                for (String condition : conditions) {
                    if (condition.startsWith("AND groupname LIKE")) {
                        // 在参数值前后添加通配符以支持模糊查询
                        ps.setString(paramIndex++, "%" + groupName + "%");
                    } else if (condition.startsWith("AND direction LIKE")) {
                        // 同样对direction进行模糊处理
                        ps.setString(paramIndex++, "%" + direction + "%");
                    }
                }
                try (ResultSet rs = ps.executeQuery()) {
                    // 处理结果集，将数据转化为Group对象并返回
                    List<Group> groups = new ArrayList<>();
                    while (rs.next()) {
                        Group group = new Group();
                        group.setDirection(rs.getString("direction"));
                        group.setGroupname(rs.getString("groupname"));
                        group.setScale(rs.getString("scale"));
                        group.setNumber(rs.getInt("number"));
                        groups.add(group);
                    }
                    return groups;
                }
            }
        }
    }

    @Override
    public Group selectById(String GroupName) {
        try {
            String sql = "SELECT groupname,number,scale,direction,Locked FROM tb_group where groupname = ?";
            return executeQueryBean(Group.class,sql,GroupName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int ChangeGroupData(String groupname, String number, String scale, String direction, String visiable) {
        try {
            String sql = "UPDATE tb_group SET number=?,scale=?,direction=?,visiable=?  WHERE groupname = ?";
            return executeUpdate(sql,number,scale,direction,visiable,groupname);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Group> selectAllForAdmin() {
        try {
            String sql = "SELECT groupname FROM tb_group ";
            return executeQuery(Group.class, sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int OperateBanGroup(String id, String action) {
        try {
            String sql = "UPDATE tb_group SET Locked=? WHERE groupname = ?";
            return executeUpdate(sql,action,id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int AgreeCreateGroup(String id) {
        try {
            String sql = "INSERT INTO tb_group(groupname,number,publicfunds) VALUES (?,?,?)";
            return executeUpdate(sql,id,1,0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Group SelectGroupPublicFund(String id) {
        try {
            String sql = "SELECT publicfunds FROM tb_group WHERE groupname=?";
            return executeQueryBean(Group.class, sql,id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

