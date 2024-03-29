package com.github.jaykkumar01.filetransfer.models;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String userId;
    private boolean isMute;
    private boolean isDeafen;
    private long joinTime;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String userId) {
        this.userId = userId;
    }

    public User(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public User(String name, String userId, boolean isMute, boolean isDeafen) {
        this.name = name;
        this.userId = userId;
        this.isMute = isMute;
        this.isDeafen = isDeafen;
    }

    public User(String name, String userId, boolean isMute, boolean isDeafen, long joinTime) {
        this.name = name;
        this.userId = userId;
        this.isMute = isMute;
        this.isDeafen = isDeafen;
        this.joinTime = joinTime;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public boolean isDeafen() {
        return isDeafen;
    }

    public void setDeafen(boolean deafen) {
        isDeafen = deafen;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}

