package com.example.sms;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class SCToast {
    public void postToastMessage(final String message, final Context context) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
