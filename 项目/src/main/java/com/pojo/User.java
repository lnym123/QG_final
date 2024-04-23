package com.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private Integer id;

    private String username;
    private String password;

    private String location;

    private String nickname;

    private String PhoneNumber;

    private int authority;
    private String groupid;

    private String avatar_url;

    public int getPersonalfunds() {
        return Personalfunds;
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

    public User(String username, String password, String location, String nickname, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.location = location;
        this.nickname = nickname;
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
                ", nickname='" + nickname + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", groupid='" + groupid + '\'' +
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }
}




