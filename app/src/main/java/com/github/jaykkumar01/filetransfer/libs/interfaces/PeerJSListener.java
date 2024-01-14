package com.github.jaykkumar01.filetransfer.libs.interfaces;

import com.github.jaykkumar01.filetransfer.models.FileData;
import com.github.jaykkumar01.filetransfer.models.User;

public interface PeerJSListener {
    void onReceiveData(User user, FileData fileData);
    void onConnect();

}
