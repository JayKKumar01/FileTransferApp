package com.github.jaykkumar01.filetransfer.libs.interfaces;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.github.jaykkumar01.filetransfer.TransferActivity;
import com.github.jaykkumar01.filetransfer.libs.utils.Base;
import com.github.jaykkumar01.filetransfer.libs.utils.ObjectUtil;
import com.github.jaykkumar01.filetransfer.models.FileData;
import com.github.jaykkumar01.filetransfer.models.User;
import com.github.jaykkumar01.filetransfer.services.TransferService;

import java.io.File;


public class JavaScriptInterface {
    private final Context context;
    private final PeerJSListener peerJSListener;
    public JavaScriptInterface(Context context, PeerJSListener peerJSListener) {
        this.context = context;
        this.peerJSListener = peerJSListener;
    }

    @JavascriptInterface
    public void onConnected(){
        peerJSListener.onConnect();
    }
    @JavascriptInterface
    public void onConnected(String id){
    }

    @JavascriptInterface
    public void receiveFileData(int fileId,byte[] fileName,String id, int index, byte[] bytes,long curSize, long size){
        FileData fileData = new FileData(fileId,index,bytes,size);
        fileData.setCurSize(curSize);
        fileData.setName(ObjectUtil.restoreString(fileName));

        peerJSListener.onReceiveData(new User(id),fileData);
    }
}
