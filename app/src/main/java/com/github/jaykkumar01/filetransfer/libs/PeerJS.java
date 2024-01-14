package com.github.jaykkumar01.filetransfer.libs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.jaykkumar01.filetransfer.libs.interfaces.JavaScriptInterface;
import com.github.jaykkumar01.filetransfer.libs.interfaces.PeerJSListener;
import com.github.jaykkumar01.filetransfer.libs.utils.Base;
import com.github.jaykkumar01.filetransfer.libs.utils.ObjectUtil;
import com.github.jaykkumar01.filetransfer.models.FileData;

public class PeerJS {
    private static final String RANDOM_PEER_STRING = "JayKKumar01-PeerJS-FileTransfer-";
    private final Context context;
    private final String id;

    private WebView webView;
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    private PeerJSListener peerJSListener;


    public PeerJS(Context context,PeerJSListener peerJSListener) {
        this.context = context;
        this.peerJSListener = peerJSListener;
        id = RANDOM_PEER_STRING+Base.generateRandomID();
        initialize();
    }
    public PeerJS(Context context,String id,PeerJSListener peerJSListener) {
        this.context = context;
        this.peerJSListener = peerJSListener;
        this.id = RANDOM_PEER_STRING+id;
        initialize();
    }

    public void sendData(FileData fileData){
        //Toast.makeText(context, fileData.getId()+" "+fileData.getIndex() + " "+ fileData.getName()+" "+fileData.getTotalSize(), Toast.LENGTH_SHORT).show();
        invoke("sendFileData",fileData.getId(),fileData.getName(),fileData.getIndex(),fileData.getBytes(),fileData.getCurSize(),fileData.getTotalSize());
    }



    private void initialize() {
        setupWebView();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void setupWebView() {
        webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                boolean x = !url.equals("file:///android_asset/call.html");
                if (x) {
                    return;
                }
                invoke("init", id);
            }

        });
        JavaScriptInterface javascriptInterface = new JavaScriptInterface(context,peerJSListener);
        webView.addJavascriptInterface(javascriptInterface, "Android");

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        String path = "file:android_asset/call.html";
        webView.loadUrl(path);


    }
    public void invoke(String func, Object... args) {
        StringBuilder argString = new StringBuilder();
        if (args.length > 0) {
            if (args[0] instanceof String) {
                argString.append(ObjectUtil.preserveString((String) args[0]));
            } else if (args[0] instanceof byte[]) {
                argString.append(ObjectUtil.preserveBytes((byte[]) args[0]));
            } else {
                argString.append(args[0]);
            }
        }
        for (int i = 1; i < args.length; i++) {
            argString.append(",");
            if (args[i] instanceof String) {
                argString.append(ObjectUtil.preserveString((String) args[i]));
            } else if (args[i] instanceof byte[]) {
                argString.append(ObjectUtil.preserveBytes((byte[]) args[i]));
            } else {
                argString.append(args[i]);
            }
        }
        final String javascriptCommand = String.format("javascript:%s(%s)", func, argString.toString());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(javascriptCommand);
            }
        });
    }


    public void connect(String otherId) {
        invoke("connect",RANDOM_PEER_STRING+otherId);
    }

    public void sendMessage(String name,String msg, long millis) {
        invoke("send",name,msg,millis);
    }

    public void setListener(PeerJSListener peerJSListener) {
        this.peerJSListener = peerJSListener;
    }
}
