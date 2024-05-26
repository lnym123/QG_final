package com.pojo;

import com.AutoValidateFrame.Inno.ContainOnly;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private Integer id;
    @ContainOnly(allowed = "^[a-zA-Z]+$",message = "用户名只能英文组成")
    private String username;
    private String password;
    @ContainOnly(allowed = "^[\\u4e00-\\u9fa5]+$",message = "地址格式错误")
    private String location;

    @ContainOnly(allowed = "^1[3-9]\\d{9}$",message = "手机号格式错误")
    private String PhoneNumber;

    private int authority;
    private String groupid;

    private String avatar_url;

    public int getPersonalfunds() {
        return Personalfunds;
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String location, String phoneNumber) {
        this.username = username;
        this.location = location;
        PhoneNumber = phoneNumber;
    }

    public void setPersonalfunds(int personalfunds) {
        Personalfunds = personalfunds;
    }

    public int getGroupfunds() {
        return Groupfunds;
    }

    public void setGroupfunds(int groupfunds) {
        Groupfunds = groupfunds;
    }

    public String getLocked() {
        return Locked;
    }

    public void setLocked(String locked) {
        Locked = locked;
    }

    private int Personalfunds;

    private int Groupfunds;

    private String Locked;

    public User(String username, String password, String location,  String phoneNumber) {
        this.username = username;
        this.password = password;
        this.location = location;
        PhoneNumber = phoneNumber;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", location='" + location + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", authority=" + authority +
                ", groupid='" + groupid + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", Personalfunds=" + Personalfunds +
                ", Groupfunds=" + Groupfunds +
                ", Locked='" + Locked + '\'' +
                '}';
    }



    public String getAvatar_url() {
        return avatar_url;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }
}




