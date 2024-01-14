package com.github.jaykkumar01.filetransfer.utils;

import androidx.annotation.NonNull;

import com.github.jaykkumar01.filetransfer.interfaces.FirebaseListener;
import com.github.jaykkumar01.filetransfer.models.EventListenerData;
import com.github.jaykkumar01.filetransfer.models.ListenerData;
import com.github.jaykkumar01.filetransfer.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FirebaseUtils {
    public static final String MEDIA_TRANSFER = "MEDIA_TRANSFER";
    private static final String USERS = "users";

    private static DatabaseReference getDatabaseReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(MEDIA_TRANSFER);
    }

    public static void checkCodeExists(String code, FirebaseListener valueEventListener) {
        DatabaseReference databaseReference = getDatabaseReference().child(code);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                valueEventListener.onComplete(snapshot.exists(),new ListenerData("Code doesn't exist"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                valueEventListener.onComplete(false,new ListenerData("Something went wrong!"));
            }
        });
    }

    public static EventListenerData getUserList(String code, FirebaseListener listener) {


        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<User> list = null;
                Set<String> idList = null;
                ListenerData data = new ListenerData();
                if (dataSnapshot.exists()) {
                    // Iterate through the children (users) and retrieve their data
                    list = new ArrayList<>();
                    idList = new HashSet<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null) {
                            list.add(user);
                            idList.add(user.getUserId());
                        }
                    }
                    if (list.isEmpty()){
                        data.setMessage("No Users found!");
                    }
                }else {
                    data.setErrorMessage("node deleted!");
                }

                data.setUserList(list);
                data.setIdList(idList);
                data.setValueEventListener(this);
                listener.onComplete(dataSnapshot.exists(),data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onComplete(false,new ListenerData("Something went wrong!"));
            }
        };
        DatabaseReference databaseReference = getDatabaseReference().child(code).child(USERS);
        databaseReference.addValueEventListener(eventListener);
        return new EventListenerData(databaseReference,eventListener);
    }
    public static void updateUserData(String path, User userModel, FirebaseListener listener) {
        DatabaseReference databaseReference = getDatabaseReference().child(path);
        String userId = userModel.getUserId();
        databaseReference.child(USERS).child(userId).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener == null){
                    return;
                }
                listener.onComplete(task.isSuccessful(),new ListenerData("Couldn't create User"));
            }
        });

    }


    public static void removeUserData(String path, User userModel) {
        DatabaseReference databaseReference = getDatabaseReference().child(path).child(USERS).child(userModel.getUserId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    databaseReference.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if needed
            }
        });
    }

    public static void deleteData(String path) {
        DatabaseReference reference = getDatabaseReference().child(path);
        reference.removeValue();
    }

    public static void write(String okay) {
        getDatabaseReference().setValue(okay);
    }
}

