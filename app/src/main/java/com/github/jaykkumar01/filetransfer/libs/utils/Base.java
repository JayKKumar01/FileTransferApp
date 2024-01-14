package com.github.jaykkumar01.filetransfer.libs.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Random;

public class Base{




    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
    public static String generateRandomID() {
        // Generate a random string
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 15;
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }



//    public static String generateRandomRoomCode(Activity activity) {
//        int randomNumber = new Random().nextInt(1000000);
//        return String.format(activity.getString(R.string._06d), randomNumber);
//    }

    public static float getZoomFactor(float value) {
        int dpi = Resources.getSystem().getDisplayMetrics().densityDpi;
        int deviceWidth = Resources.getSystem().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? Resources.getSystem().getDisplayMetrics().heightPixels : Resources.getSystem().getDisplayMetrics().widthPixels;

        return (float) (value * (double) deviceWidth / dpi * 0.18608773);
    }




}
