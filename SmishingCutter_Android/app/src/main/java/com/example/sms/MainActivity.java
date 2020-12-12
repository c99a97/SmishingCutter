package com.example.sms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    public static Context context;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        checkPermission();

        db = openOrCreateDatabase("PHONE_NO", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS PHONE_NO(phone_no TEXT PRIMARY KEY);");
        Cursor cursor = db.rawQuery("SELECT phone_no from PHONE_NO;", null);
        if(!cursor.moveToNext()){
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            db.execSQL("INSERT INTO PHONE_NO VALUES("+tm.getLine1Number()+");");
            Log.d(this.getClass().getName(), "Can't find User's Phone Number");
        } else{
            Log.d(this.getClass().getName(), "Find User's Phone Number");
            Toast.makeText(context, "스미싱커터는 자동실행되는 앱입니다.", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode==1){
            int length = permissions.length;
            for(int i=0; i<length; i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    Log.d(this.getClass().getName(), "권한 허용 :"+permissions[i]);
                }
            }
        }
    }

    public void checkPermission(){
        String temp = "";

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.RECEIVE_SMS+" ";
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)!=PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.READ_SMS+" ";
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.READ_PHONE_STATE+" ";
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)!=PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.READ_PHONE_NUMBERS+" ";
        }
        checkPermissionOverlay();

        if(TextUtils.isEmpty(temp) == false){
            ActivityCompat.requestPermissions(this, temp.trim().split(" "), 1);
        } else{
            Log.d(this.getClass().getName(), "권한이 모두 허용됐습니다!");
        }
    }

    public void checkPermissionOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
            PermissionOverlay();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void PermissionOverlay() {
        Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(overlayIntent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}
