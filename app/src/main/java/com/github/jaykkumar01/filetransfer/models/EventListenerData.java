package com.github.jaykkumar01.filetransfer.models;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class EventListenerData {
    Context context;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    public EventListenerData(DatabaseReference databaseReference, ValueEventListener valueEventListener) {
        this.databaseReference = databaseReference;
        this.valueEventListener = valueEventListener;
    }

    public EventListenerData() {

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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
}
