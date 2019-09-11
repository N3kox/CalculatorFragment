package com.example.fragmenttest;

//9.7 转屏数据传递完成
//9.6 负数处理完成
//9.5 没做负数处理


import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //fragment初始化
    Fragment1 fragment1 = new Fragment1();
    Fragment2 fragment2 = new Fragment2();
    //转屏数据传递
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("#Fragment","main on Create");

        //宽度高度比较进行横竖屏感知
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (dm.widthPixels > dm.heightPixels) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment1).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment2).commit();
        }
    }

    //处理转屏主activity多次oncreate问题
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            //Toast.makeText(getApplicationContext(),"横屏",Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment1).commit();
        }
        else{
            //Toast.makeText(getApplicationContext(),"竖屏",Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment2).commit();
        }
    }


    public Bundle getBundle(){
        return bundle;
    }

    public void setBundle(Bundle bundle){
        this.bundle = bundle;
    }
}
