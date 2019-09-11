package com.example.fragmenttest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class currency extends AppCompatActivity implements View.OnClickListener {

    private int EDIT_OK = 1;
    private String TAG = "currency";
    private Button fromCurrencyType,toCurrencyType;
    private String[] currencyNameList;
    private Bundle args;
    private ArrayList<currencyItem> currencyList;
    private EditText fromCurrencyCount;
    private String fromCurrencyName = "人民币",toCurrencyName = "人民币";
    private float fBuyPri,fSellPri;

    private int a,b,temprary;
    private TextView fromTypeName,toTypeName,toCurrencyCount;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        fBuyPri = 100;
        fSellPri = 100;
        a=0;
        b=0;

        fromCurrencyCount = findViewById(R.id.fromCurrencyCount);
        toCurrencyCount = findViewById(R.id.toCurrencyCount);
        fromCurrencyType = findViewById(R.id.fromCurrencyType);
        toCurrencyType = findViewById(R.id.toCurrencyType);
        fromTypeName = findViewById(R.id.fromTypeName);
        toTypeName = findViewById(R.id.toTypeName);

        Intent intent = getIntent();
        args = intent.getBundleExtra("BUNDLE");
        currencyList = (ArrayList<currencyItem>)args.getSerializable("ARRAYLIST");


        currencyNameList = new String[currencyList.size() + 1];
        currencyNameList[0] = "人民币";
        for(int i = 0;i<currencyList.size();i++){
            currencyNameList[i+1] = currencyList.get(i).getName();
            //Log.d("#JSON",currencyList.get(i).getName());
            //Log.d("#JSON",Float.toString(currencyList.get(i).getfBuyPri()));
            //Log.d("#JSON", Float.toString(currencyList.get(i).getfSellPri()));
        }
        for(int i = 0;i<currencyNameList.length;i++){
            Log.d("#JSON",currencyNameList[i]);
        }

        fromCurrencyCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 800);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
        fromCurrencyType.setOnClickListener(this);
        toCurrencyType.setOnClickListener(this);

    }
    private void cal(){
        try{

            Double result = Double.parseDouble(fromCurrencyCount.getText().toString());
            if(fBuyPri != 100){
                result *= (fBuyPri/100);
            }
            if(fSellPri != 100){
                result /= (fSellPri/100);
            }
            toCurrencyCount.setText(String.format("%.6f",result));
        }catch(Exception e){
            toCurrencyCount.setText("输入错误");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d(TAG,"what?"+msg.what);
            if (EDIT_OK == msg.what) {
                Log.d(TAG, "handleMessage() returned:输入完成 " );
                Log.d(TAG,"result:"+fromCurrencyCount.getText());
                cal();
            }

        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(EDIT_OK);
        }
    };

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fromCurrencyType:{

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("原货币类型");
                builder.setSingleChoiceItems(currencyNameList, a, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        temprary = i;
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        a = temprary;
                        if(a == 0){
                            fromCurrencyName = "人民币";
                            fBuyPri = 100;
                            fromTypeName.setText("人民币");
                        }
                        else{
                            fromCurrencyName = currencyList.get(a-1).getName();
                            fBuyPri = currencyList.get(a-1).getfBuyPri();
                            fromTypeName.setText(fromCurrencyName);
                        }
                        Log.d("#OOO","btn1:"+fromCurrencyName + "||" + fBuyPri);
                        dialogInterface.dismiss();
                        if(fromCurrencyCount.getText().toString().length()>0)
                            cal();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
                break;
            }
            case R.id.toCurrencyType:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("原货币类型");
                builder.setSingleChoiceItems(currencyNameList, a, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        temprary = i;
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        b = temprary;
                        if(b == 0){
                            toCurrencyName = "人民币";
                            fSellPri = 100;
                            toTypeName.setText(toCurrencyName);
                        }
                        else{
                            toCurrencyName = currencyList.get(b-1).getName();
                            fSellPri = currencyList.get(b-1).getfBuyPri();
                            toTypeName.setText(toCurrencyName);
                        }
                        Log.d("#OOO","btn2:"+toCurrencyName + "||" + fSellPri);
                        dialogInterface.dismiss();
                        if(fromCurrencyCount.getText().toString().length()>0)
                            cal();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
                break;
            }
        }

    }
}
