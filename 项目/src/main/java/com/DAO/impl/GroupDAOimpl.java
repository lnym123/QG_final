package com.DAO.impl;

import com.DAO.BaseDAO;
import com.DAO.GroupDAO;
import com.DAO.UserDAO;
import com.alibaba.druid.util.StringUtils;
import com.pojo.Group;
import com.pojo.User;
import com.util.JDBCUtilV2;

import java.io.IOException;
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
            String sql = "SELECT groupname,number,scale,direction FROM tb_group WHERE visible = ?";
            return executeQuery(Group.class, sql, "yes");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Group> selectBycondition(String groupName, String direction) throws SQLException {
        String SQL_SELECT_GROUP = "SELECT groupname,number,direction,scale FROM tb_group WHERE 1=1 %s";
        List<String> conditions = new ArrayList<>();

        // 根据groupName添加条件
        if (!StringUtils.isEmpty(groupName)) {
            conditions.add("AND groupname = ?");
        }

        // 根据direction添加条件
        if (!StringUtils.isEmpty(direction)) {
            conditions.add("AND direction = ?");
        }

        // 拼接查询条件
        String whereClause = conditions.stream().collect(Collectors.joining(" "));

        // 构建最终SQL语句
        String sql = String.format(SQL_SELECT_GROUP, whereClause);

        try (Connection connection = JDBCUtilV2.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int paramIndex = 1;
                for (String condition : conditions) {
                    if (condition.startsWith("AND groupname")) {
                        ps.setString(paramIndex++, groupName);
                    } else if (condition.startsWith("AND direction")) {
                        ps.setString(paramIndex++, direction);
                    }
                }
                try (ResultSet rs = ps.executeQuery()) {
                    // 处理结果集，将数据转化为Group对象并返回
                    List<Group> groups = new ArrayList<>();
                    while (rs.next()) {
                        // 将结果集数据映射到Group对象，此处省略具体映射逻辑
                        Group group = new Group();

                        // 假设Group类有以下属性，且对应的列名分别是"group_id"、"group_name"、"description"
                        group.setDirection(rs.getString("direction"));
                        group.setGroupname(rs.getString("groupname"));
                        group.setScale(rs.getString("scale"));
                        group.setNumber(rs.getInt("number"));
                        groups.add(group);
                    }
                    rs.close();
                    ps.close();
                    if (connection.getAutoCommit()) {
                        JDBCUtilV2.release();
                    }
                    return groups;

                }
            }
        }
    }

    @Override
    public Group selectById(String GroupName) {
        try {
            String sql = "SELECT groupname,number,scale,direction FROM tb_group where groupname = ?";
            return executeQueryBean(Group.class,sql,GroupName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

