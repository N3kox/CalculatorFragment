package com.example.fragmenttest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

public class calculateLength extends AppCompatActivity implements View.OnClickListener{


    private int EDIT_OK = 1;
    private String TAG = "#LENGTH";

    Button lengthFromButton,lengthNowButton;
    TextView lengthFromType,lengthNowType,lengthOutput;
    EditText lengthInput;

    int fromChoose = 0,nowChoose = 0;
    String[] aList = new String[]{"米","分米","厘米","毫米"};
    float[] bList = new float[]{1,10,100,1000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_length);
        init();
    }

    public void init(){
        lengthFromButton = findViewById(R.id.lengthFromButton);
        lengthNowButton = findViewById(R.id.lengthNowButton);
        lengthFromType = findViewById(R.id.lengthFromType);
        lengthNowType = findViewById(R.id.lengthNowType);
        lengthInput = findViewById(R.id.lengthInput);
        lengthInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable,800);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        lengthOutput = findViewById(R.id.lengthOutput);
        lengthFromButton.setOnClickListener(this);
        lengthNowButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lengthFromButton:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("原单位");
                builder.setSingleChoiceItems(aList, fromChoose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fromChoose = i;
                        lengthFromType.setText(aList[fromChoose]);
                        if(!lengthInput.getText().toString().equals(""))
                            calLength();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
                break;
            }
            case R.id.lengthNowButton:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("新单位");
                builder.setSingleChoiceItems(aList, nowChoose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nowChoose = i;
                        lengthNowType.setText(aList[nowChoose]);
                        if(!lengthInput.getText().toString().equals(""))
                            calLength();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
                break;
            }
        }
    }

    private void calLength(){
        Double result = Double.parseDouble(lengthInput.getText().toString());
        result /= bList[fromChoose];
        result *= bList[nowChoose];
        lengthOutput.setText(Double.toString(result));
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d(TAG,"what?"+msg.what);
            if (EDIT_OK == msg.what) {
                Log.d(TAG, "handleMessage() returned:输入完成 " );
                Log.d(TAG,"result:"+lengthInput.getText());
                calLength();
            }

        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(EDIT_OK);
        }
    };

}
