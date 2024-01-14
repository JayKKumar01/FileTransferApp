package com.github.jaykkumar01.filetransfer.models;


import com.github.jaykkumar01.filetransfer.enums.RoomType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    User user;
    String code;
    RoomType roomType;
    boolean peerConnected;
    List<User> userList = new ArrayList<>();


    public Room() {
    }

    public Room(User user, String code) {
        this.user = user;
        this.code = code;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public boolean isPeerConnected() {
        return peerConnected;
    }

    public void setPeerConnected(boolean peerConnected) {
        this.peerConnected = peerConnected;
    }

    public User getUser() {
        return user;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
