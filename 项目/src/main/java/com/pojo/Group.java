package com.pojo;

public class Group {
    private Integer id;
    private String groupname;
    private int number;
    private String scale;
    private String direction;

    @Override
    public String toString() {
        return "Group{" +
                "groupname='" + groupname + '\'' +
                ", number='" + number + '\'' +
                ", scale='" + scale + '\'' +
                ", direction='" + direction + '\'' +
                '}';
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
