package com.github.jaykkumar01.filetransfer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.github.jaykkumar01.filetransfer.R;
import com.github.jaykkumar01.filetransfer.TransferActivity;
import com.github.jaykkumar01.filetransfer.enums.RoomType;
import com.github.jaykkumar01.filetransfer.libs.PeerJS;
import com.github.jaykkumar01.filetransfer.libs.interfaces.PeerJSListener;
import com.github.jaykkumar01.filetransfer.models.FileData;
import com.github.jaykkumar01.filetransfer.models.FileMap;
import com.github.jaykkumar01.filetransfer.models.Room;
import com.github.jaykkumar01.filetransfer.models.User;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class TransferService extends Service {
    private static final String CHANNEL_ID = "JayKKumar01-FileTransfer";
    private static final CharSequence CHANNEL_NAME = "JayKKumar01-TransferChannel";
    private static final int NOTIFICATION_ID = 10;
    private Room room;
    private User user;
    private PeerJS peerJS;
    public static Listener listener;
    private final HashMap<Integer, FileMap> files = new HashMap<>();

    public interface Listener{
        void onSendData(FileData fileData);

        void onSendMessage(String message, long currentTimeMillis);
    }

    PeerJSListener peerJSListener = new PeerJSListener() {

        @Override
        public void onConnect() {
            Toast.makeText(TransferService.this, "Connected", Toast.LENGTH_SHORT).show();
            for (User user: room.getUserList()){
                peerJS.connect(user.getUserId());
            }
        }

        @Override
        public void onReceiveData(User user, FileData fileData) {
            if (fileData == null) {
                // Handle the case where fileData is null
                return;
            }

            int fileId = fileData.getId();
            String name = fileData.getName();
            long index = fileData.getIndex();

            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file;
            FileMap fileMap;

            if (files.containsKey(fileId)) {
                fileMap = files.get(fileId);
                if (fileMap == null) {
                    return;
                }
                file = new File(folder, fileMap.getName());
            } else {
                file = generateUniqueFile(folder, name);
                fileMap = new FileMap(file.getName(), 0);
                fileMap.setId(fileId);
                files.put(fileId, fileMap);
            }

            boolean success = writeBytesToFile(file, index, fileData.getBytes());

            if (!success) {
                Toast.makeText(TransferService.this, "Failed to save file!", Toast.LENGTH_SHORT).show();
                return;
            }

            updateFileMapAndNotifyProgress(fileMap, fileData.getTotalSize(), fileData.getCurSize());
        }

        private File generateUniqueFile(File folder, String name) {
            int i = 0;
            String fileName = name;
            File file;

            while ((file = new File(folder, fileName)).exists()) {
                fileName = generateUniqueFileName(name, ++i);
            }

            return file;
        }

        private String generateUniqueFileName(String name, int count) {
            if (name.contains(".")) {
                String ext = name.substring(name.lastIndexOf("."));
                String fileName = name.replace(ext, "");
                return fileName + " (" + count + ")" + ext;
            } else {
                return name + " (" + count + ")";
            }
        }

        private boolean writeBytesToFile(File file, long index, byte[] bytes) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
                randomAccessFile.seek(index);
                randomAccessFile.write(bytes);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        private void updateFileMapAndNotifyProgress(FileMap fileMap, long totalSize, long bytesWritten) {
            fileMap.setTotalSize(fileMap.getTotalSize() + bytesWritten);

            if (totalSize > 0) {
                float progress = (float) fileMap.getTotalSize() / totalSize;
                TransferActivity.listener.onProgress(fileMap, progress * 100);
            }
        }



//        @Override
//        public void onReceiveData(User user, FileData fileData) {
//
//            int fileId = fileData.getId();
//            String name = fileData.getName();
//            long index = fileData.getIndex();
//
//            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//            File file;
//            FileMap fileMap;
//            if (files.containsKey(fileId)) {
//                fileMap = files.get(fileId);
//                if (fileMap == null){
//                    return;
//                }
//                file = new File(folder, fileMap.getName());
//            } else {
//                file = new File(folder, name);
//                int i = 0;
//                String fileName = name;
//                while (file.exists()) {
//                    String ext = "";
//                    if (name.contains(".")){
//                        ext = name.substring(name.lastIndexOf("."));
//                        fileName = name.replace(ext,"");
//                    }
//                    fileName = fileName + " (" + ++i + ")" + ext;
//                    file = new File(folder, fileName);
//                }
//                fileMap = new FileMap(fileName,0);
//                fileMap.setId(fileId);
//                files.put(fileId, fileMap);
//            }
//            boolean success = false;
//            byte[] bytes = fileData.getBytes();
//            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
//
//                randomAccessFile.seek(index);
//                randomAccessFile.write(bytes);
//                success = true;
//                // using FileData size and filemap total size toast a progress in percentage
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (!success){
//                Toast.makeText(TransferService.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
//                return;
//            }
////            Toast.makeText(TransferService.this, "Saved!", Toast.LENGTH_SHORT).show();
//            fileMap.setTotalSize(fileMap.getTotalSize() + bytes.length);
//            float progress = (float) fileMap.getTotalSize() / fileData.getTotalSize();
//            TransferActivity.listener.onProgress(fileMap, progress * 100);
//            // Write the file with the help of RandomAccessFile
//
//        }


    };

    private void showMessage(String name) {

    }

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        room = (Room) intent.getSerializableExtra(getString(R.string.room));
        if (room == null){
            return START_NOT_STICKY;
        }
        setUpListener();
        user = room.getUser();
        createNotification(user);
        peerJS = new PeerJS(this,user.getUserId(),peerJSListener);


        return START_NOT_STICKY;
    }

    private void setUpListener() {
        listener = new Listener() {
            @Override
            public void onSendData(FileData fileData) {
                peerJS.sendData(fileData);
            }

            @Override
            public void onSendMessage(String message, long currentTimeMillis) {
                peerJS.sendMessage(user.getName(),message,currentTimeMillis);
            }
        };
    }

    private void createNotification(User user) {

        room.setRoomType(RoomType.PENDING);

        Intent callIntent = new Intent(this, TransferActivity.class);
        callIntent.putExtra(getString(R.string.room), room);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, callIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // Create an explicit intent for the activity that handles the button actions
//        Intent intent = new Intent(this, NotificationReceiver.class);
//        intent.setAction("com.github.jaykkumar01.watchparty.receivers.ACTION_MUTE_HANGUP");
//        intent.putExtra("requestCode", REQUEST_CODE_MUTE);
//
//        mutePendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_MUTE, intent, PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);
//        intent.putExtra("requestCode", REQUEST_CODE_HANGUP);
//        hangupPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_HANGUP, intent, PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);
//        intent.putExtra("requestCode",REQUEST_CODE_DEAFEN);
//        deafenPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_DEAFEN, intent, PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);
//
//        String muteLabel = Info.isMute? "Unmute" : "Mute";
//        String deafenLabel = Info.isDeafen? "Undeafen" : "Deafen";
        // Create the notification
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.transfer)
                .setContentTitle("Transfer Channel Active")
                .setContentText("Tap to manage transfer")
                .setPriority(Notification.PRIORITY_DEFAULT)
//                .addAction(R.drawable.call_end, "Disconnect", hangupPendingIntent)
//                .addAction(R.drawable.mic_on, muteLabel, mutePendingIntent)
//                .addAction(R.drawable.deafen_on, deafenLabel, deafenPendingIntent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L});

        //notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the notification channel for Android Oreo and above
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("channelDescription");
                channel.setVibrationPattern(new long[]{0L});
                channel.enableVibration(true);

                notificationManager.createNotificationChannel(channel);
            }
            startForeground(NOTIFICATION_ID, builder.build());
            notificationManager.notify(NOTIFICATION_ID, builder.build());



        }
    }

    public void sendMessage(View view){
        peerJS.sendMessage("id","message "+System.currentTimeMillis(),System.currentTimeMillis());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}