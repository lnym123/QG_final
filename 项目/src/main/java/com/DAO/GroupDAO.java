package com.DAO;

import com.pojo.Group;

import java.sql.SQLException;
import java.util.List;

public interface GroupDAO {

     List<Group> selectAll();

     List<Group> selectBycondition(String groupName, String direction) throws SQLException;

     Group selectById(String GroupName);

}