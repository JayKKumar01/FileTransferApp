package com.github.jaykkumar01.filetransfer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jaykkumar01.filetransfer.managers.UserListManager;
import com.github.jaykkumar01.filetransfer.models.FileData;
import com.github.jaykkumar01.filetransfer.models.FileMap;
import com.github.jaykkumar01.filetransfer.models.Room;
import com.github.jaykkumar01.filetransfer.models.User;
import com.github.jaykkumar01.filetransfer.services.TransferService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class TransferActivity extends AppCompatActivity {
    private Room room;
    private User user;


    private UserListManager userListManager;
    private RecyclerView recyclerView;
    private TextView codeView;

    public static Listener listener;
    private static final Random random = new Random();
    private TextView progressTV;


    public interface Listener{
        void onReceiveData(User user,FileData fileData);

        void onProgress(FileMap fileMap, float progress);
    }

    private Uri fileUri;
    private TextView currentFileTV;
    private String fileName;
    ActivityResultLauncher<Intent> pickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data != null) {
                            fileUri = data.getData();
                            if (fileUri != null) {
                                try {
                                    Cursor returnCursor =
                                            getContentResolver().query(fileUri, null, null, null, null);
                                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    returnCursor.moveToFirst();
                                    fileName = returnCursor.getString(nameIndex);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String name = fileUri.toString();
                                if (name.contains("/")){
                                    name = name.substring(name.lastIndexOf("/")+1);
                                }
                                if (fileName != null){
                                    name = fileName;
                                }
                                currentFileTV.setText(name);
                            }
                        }
                    }

                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        room = (Room) getIntent().getSerializableExtra(getString(R.string.room));
        if (room == null){
            return;
        }
        setUpListener();
//        TransferService.listener.onSendData();
        user = room.getUser();
        recyclerView = findViewById(R.id.recyclerView);
        codeView = findViewById(R.id.code);
        currentFileTV = findViewById(R.id.selectFile);
        progressTV = findViewById(R.id.progress);
        codeView.setText(room.getCode());
        userListManager = new UserListManager(this,recyclerView,room);


    }
    @SuppressLint("SetTextI18n")

    private void setUpListener() {
        listener = new Listener() {
            @Override
            public void onReceiveData(User user, FileData fileData) {

            }

            @Override
            public void onProgress(FileMap fileMap, float progress) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (fileMap != null){
                            int fileId = fileMap.getId();
                            String name = fileMap.getName();
                        }

                        progressTV.setText(progress + " %");
                    }
                });
            }
        };
    }


    public void selectFile(View view) {
        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.setType("*/*");
//        data.setType("video/*");
//        data.addCategory(Intent.CATEGORY_OPENABLE);
        data = Intent.createChooser(data,"File Picker");
        pickerLauncher.launch(data);
    }
    public void send(View view) {
        //    ...
        //    /*
        //     * Get the file's content URI from the incoming Intent,
        //     * then query the server app to get the file's display name
        //     * and size.
        //     */
        //    Uri returnUri = returnIntent.getData();
//            Cursor returnCursor =
//                    getContentResolver().query(returnUri, null, null, null, null);
//            /*
//             * Get the column indexes of the data in the Cursor,
//             * move to the first row in the Cursor, get the data,
//             * and display it.
//             */
//            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//            returnCursor.moveToFirst();
//            TextView nameView = (TextView) findViewById(R.id.filename_text);
//            TextView sizeView = (TextView) findViewById(R.id.filesize_text);
//            nameView.setText(returnCursor.getString(nameIndex));
//            sizeView.setText(Long.toString(returnCursor.getLong(sizeIndex)));
        //    ...

        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null){
                Toast.makeText(this, "Access Denied!", Toast.LENGTH_SHORT).show();
                return;
            }
            long fileSize = inputStream.available();
            int id = random.nextInt(1000000);
            int bufferSize = 1024*256;
            byte[] buffer = new byte[bufferSize];

            int i = 0;
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {

                FileData fileData = new FileData(id,i,buffer,fileSize);
                fileData.setName(fileName == null ? System.currentTimeMillis()+"" : fileName);
                fileData.setCurSize(len);
                TransferService.listener.onSendData(fileData);
                i += len;
                float progress = (float) i / fileSize;
                listener.onProgress(null,progress * 100);
            }
            inputStream.close();

        } catch (IOException e) {
            Toast.makeText(this, "File not readable", Toast.LENGTH_SHORT).show();
        }
    }
}