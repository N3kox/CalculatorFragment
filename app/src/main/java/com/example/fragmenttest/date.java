package com.example.fragmenttest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class date extends AppCompatActivity implements View.OnClickListener {

    Button dateFromBtn,dateToBtn;
    TextView dateFromText,dateToText,dateCalculate;

    int y1,m1,d1,y2,m2,d2;
    boolean c1 = false,c2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        y1 = y2 = 2019;
        m1 = m2 = 9;
        d1 = d2 = 10;

        dateFromBtn = findViewById(R.id.dateFromBtn);
        dateToBtn = findViewById(R.id.dateToBtn);
        dateFromText = findViewById(R.id.dateFromText);
        dateToText = findViewById(R.id.dateToText);
        dateCalculate = findViewById(R.id.dateCalculate);

        dateFromBtn.setOnClickListener(this);
        dateToBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.dateFromBtn:{
                DatePickerDialog datePickerDialog = new DatePickerDialog(date.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        y1 = i;
                        m1 = i1;
                        d1 = i2;
                        c1 = true;
                        dateFromText.setText("从"+y1+"年"+m1+"月"+d1+"日");
                        if(c1 && c2){
                            cal();
                        }
                    }
                },y1,m1,d1);
                datePickerDialog.show();
                break;
            }
            case R.id.dateToBtn:{
                DatePickerDialog datePickerDialog = new DatePickerDialog(date.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        y2 = i;
                        m2 = i1;
                        d2 = i2;
                        c2 = true;
                        dateToText.setText("至"+y2+"年"+m2+"月"+d2+"日");
                        if(c1 && c2){
                            cal();
                        }
                    }
                },y2,m2,d2);
                datePickerDialog.show();
                break;
            }
        }
    }
    public void cal(){
        //Log.d("#Date","开始运算");

        LocalDate start = LocalDate.of(y1, m1,d1);
        LocalDate end = LocalDate.of(y2,m2,d2);
        long between = ChronoUnit.DAYS.between(start,end);
        dateCalculate.setText("相差"+Long.toString(between)+"天");
    }
}
