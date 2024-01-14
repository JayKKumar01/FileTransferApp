package com.github.jaykkumar01.filetransfer.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jaykkumar01.filetransfer.R;
import com.github.jaykkumar01.filetransfer.adapters.UserAdapter;
import com.github.jaykkumar01.filetransfer.models.EventListenerData;
import com.github.jaykkumar01.filetransfer.models.Room;
import com.github.jaykkumar01.filetransfer.models.User;
import com.github.jaykkumar01.filetransfer.services.TransferService;
import com.github.jaykkumar01.filetransfer.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserListManager {
    Context context;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    Room room;

    List<User> userList = new ArrayList<>();

    public UserListManager(Context context, RecyclerView recyclerView, Room room) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.room = room;
        initialize();
    }

    private void initialize() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        userAdapter = new UserAdapter();
        recyclerView.setAdapter(userAdapter);

        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
        EventListenerData listenerData = FirebaseUtils.getUserList(room.getCode(), (successful, data) -> {
            if (successful){
                Set<String> idList = data.getIdList();
                for (User user : userList){
                    if (!user.getUserId().equals(user.getUserId()) && !idList.contains(user.getUserId())){
                        Toast.makeText(context, user.getName()+" got disconnected!", Toast.LENGTH_SHORT).show();
                    }
                }
                userList = data.getUserList();

                if (!room.isPeerConnected()){
                    room.setPeerConnected(true);
                    room.setUserList(userList);
                    Intent serviceIntent = new Intent(context, TransferService.class);
                    serviceIntent.putExtra(context.getString(R.string.room),room);
                    context.startService(serviceIntent);
                }

//                PeerManagement.listener.onUpdatePeerCount(userList.size());

                userAdapter.setList(userList);
                userAdapter.notifyDataSetChanged();

                if (userList.isEmpty()){
                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, data.getErrorMessage()+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
