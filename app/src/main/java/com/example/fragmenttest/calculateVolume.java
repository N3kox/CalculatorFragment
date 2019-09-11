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

public class calculateVolume extends AppCompatActivity implements View.OnClickListener{


    private int EDIT_OK = 1;
    private String TAG = "#VOLUME";

    Button volumeFromButton,volumeNowButton;
    TextView volumeFromType,volumeNowType,volumeOutput;
    EditText volumeInput;

    int fromChoose = 0,nowChoose = 0;
    String[] aList = new String[]{"立方米","百升/公石","升/立方分米","分升","厘升","立方厘米"};
    float[] bList = new float[]{1,10,1000,10000,100000,1000000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_volume);
        init();
    }

    public void init(){
        volumeFromButton = findViewById(R.id.volumeFromButton);
        volumeNowButton = findViewById(R.id.volumeNowButton);
        volumeFromType = findViewById(R.id.volumeFromType);
        volumeNowType = findViewById(R.id.volumeNowType);
        volumeInput = findViewById(R.id.volumeInput);
        volumeInput.addTextChangedListener(new TextWatcher() {
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
        volumeOutput = findViewById(R.id.volumeOutput);
        volumeFromButton.setOnClickListener(this);
        volumeNowButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.volumeFromButton:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("原单位");
                builder.setSingleChoiceItems(aList, fromChoose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fromChoose = i;
                        volumeFromType.setText(aList[fromChoose]);
                        if(!volumeInput.getText().toString().equals(""))
                            calVolume();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
                break;
            }
            case R.id.volumeNowButton:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("新单位");
                builder.setSingleChoiceItems(aList, nowChoose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nowChoose = i;
                        volumeNowType.setText(aList[nowChoose]);
                        if(!volumeInput.getText().toString().equals(""))
                            calVolume();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
                break;
            }
        }
    }

    private void calVolume(){
        Double result = Double.parseDouble(volumeInput.getText().toString());
        result /= bList[fromChoose];
        result *= bList[nowChoose];
        volumeOutput.setText(Double.toString(result));
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d(TAG,"what?"+msg.what);
            if (EDIT_OK == msg.what) {
                Log.d(TAG, "handleMessage() returned:输入完成 " );
                Log.d(TAG,"result:"+volumeInput.getText());
                calVolume();
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
