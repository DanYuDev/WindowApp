package com.example.coderlt.windowapp;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String TAG=getClass().getName();
    private static final int REQUEST_WINDOW=1;
    private Button mFloatingButton;
    private float originX,originY;
    private int[] loc=new int[2];

    private WindowManager wm;
    private WindowManager.LayoutParams mLayoutParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFloatingButton=new Button(this);
        mFloatingButton.setText("mFloatingButton");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(!Settings.canDrawOverlays(this)){
                Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivityForResult(intent,REQUEST_WINDOW);
            }
        }
        else addFloatingButton();

        // 拖动是可以了，但是 ACTION_DOWN 的时候，fb总要跳一下
        mFloatingButton.setOnTouchListener(new View.OnTouchListener() {
            float oldX,oldY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x=event.getRawX();
                float y=event.getRawY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG,"ACTION_DOWN...");
                        oldX=event.getRawX();
                        oldY=event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        mLayoutParams.x=(int)(x-oldX+originX);
                        mLayoutParams.y=(int)(y-oldY+originY);
                        wm.updateViewLayout(mFloatingButton,mLayoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG,"ACTION_UP...");
                        // TODO 这个originX 和 originY 怎么直接通过 mFloatingButton 来获取呢
                        originX=mLayoutParams.x;
                        originY=mLayoutParams.y;
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(Settings.canDrawOverlays(this)){
                Log.d(TAG,"Permission granted!");
                addFloatingButton();
            }
        }else {
            Toast.makeText(this,"OverLay permission denied",Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void addFloatingButton(){
        mLayoutParams=new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,0, PixelFormat.TRANSPARENT
        );

        mLayoutParams.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        mLayoutParams.gravity= Gravity.LEFT|Gravity.TOP;
        mLayoutParams.x=100;
        mLayoutParams.y=300;
        originX=100;
        originY=300;
        wm=getWindowManager();
        wm.addView(mFloatingButton,mLayoutParams);
    }
}
