package com.example.fragmenttest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class calculateHistory extends AppCompatActivity {

    ArrayList<String>logCal = new ArrayList<>();
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_history);

        tv = findViewById(R.id.logDetail);


        Intent intent = getIntent();
        String showList = "";
        logCal = intent.getStringArrayListExtra("LOG");
        if(logCal.size() == 0)
            showList = "暂无数据";
        else {
            for (int i = 0; i < logCal.size(); i++) {
                Log.d("#LOG", logCal.get(i));
                showList += logCal.get(i) + "\n";
            }
        }
        tv.setText(showList);

    }
}
