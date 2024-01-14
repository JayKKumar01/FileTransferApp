package com.github.jaykkumar01.filetransfer.interfaces;


import com.github.jaykkumar01.filetransfer.models.ListenerData;

public interface FirebaseListener {
    void onComplete(boolean successful, ListenerData data);
}
