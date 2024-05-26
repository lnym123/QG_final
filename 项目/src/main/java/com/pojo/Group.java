package com.pojo;

import com.AutoValidateFrame.Inno.ContainOnly;

public class Group {
    private Integer id;
    @ContainOnly(allowed = "^[\\u4e00-\\u9fa5]+$",message = "群组名称格式错误")
    private String groupname;
    private int number;
    @ContainOnly(allowed = "^[\\u4e00-\\u9fa5]+$",message = "企业规模格式错误")
    private String scale;
    @ContainOnly(allowed = "^[\\u4e00-\\u9fa5]+$",message = "企业方向格式错误")
    private String direction;
    private int publicfunds;
    private String Locked;

    public String getLocked() {
        return Locked;
    }

    public void setLocked(String locked) {
        Locked = locked;
    }

    public Group(String groupname, String scale, String direction) {
        this.groupname = groupname;
        this.scale = scale;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupname='" + groupname + '\'' +
                ", number=" + number +
                ", scale='" + scale + '\'' +
                ", direction='" + direction + '\'' +
                ", publicfunds=" + publicfunds +
                ", Locked='" + Locked + '\'' +
                '}';
    }

    public int getPublicfunds() {
        return publicfunds;
    }

    public void setPublicfunds(int publicfunds) {
        this.publicfunds = publicfunds;
    }

    public Group() {
    }

    public String getGroupname() {
        return groupname;
    }

    public Group(String groupname, int number, String scale, String direction) {
        this.groupname = groupname;
        this.number = number;
        this.scale = scale;
        this.direction = direction;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
