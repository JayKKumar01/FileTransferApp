package com.github.jaykkumar01.filetransfer.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Set;

public class ListenerData {
    String message;
    String errorMessage;
    List<User> userList;
    Set<String> idList;

    ValueEventListener valueEventListener;
    DatabaseReference databaseReference;


    public Set<String> getIdList() {
        return idList;
    }

    public void setIdList(Set<String> idList) {
        this.idList = idList;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public ValueEventListener getValueEventListener() {
        return valueEventListener;
    }

    public void setValueEventListener(ValueEventListener valueEventListener) {
        this.valueEventListener = valueEventListener;
    }

    public ListenerData() {
    }

    public ListenerData(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ListenerData(String message, String errorMessage) {
        this.message = message;
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public ListenerData(String message, String errorMessage, List<User> userList) {
        this.message = message;
        this.errorMessage = errorMessage;
        this.userList = userList;
    }


}
