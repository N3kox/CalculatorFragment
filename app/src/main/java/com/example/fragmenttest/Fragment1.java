package com.example.fragmenttest;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {

    final String TAG= "#Main";
    EditText all_input,cur_input;
    Button btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9,btn_0;
    Button btn_left_bracket,btn_right_bracket;
    Button btn_point,btn_plus,btn_minus,btn_multiply,btn_divide,btn_equal;
    Button btn_sin,btn_cos,btn_tan;
    Button btn_square,btn_sqrt;
    Button btn_reverse;
    Button btn_del,btn_dec;
    String allInput="",curInput = "0";
    ArrayList<String> infix = new ArrayList<String>();
    ArrayList<String> suffix = new ArrayList<String>();
    Stack<String> calStack = new Stack<>();
    boolean pushMark = false;
    int leftBracketCount = 0,rightBracketCount = 0;
    //进制-默认10进制
    int fromHEX = 10,toHEX = 10;

    Bundle args = new Bundle();
    ArrayList<currencyItem> currencyList = new ArrayList<currencyItem>();
    ArrayList<String> logCal = new ArrayList<>();


    public Fragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);
        Log.d("#Fragment1","Fragment1 init");
        sendRequestWithOkHttp();
        setHasOptionsMenu(true);
        init(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_1,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.help:{
                //Toast.makeText(getActivity(),"帮助!!!",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("帮助");
                builder.setMessage("混乱计算器");
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
                break;
            }
            case R.id.HEX:{
                initHexAlertDialog();
                break;
            }
            case R.id.CURRENCY:{
                Intent intent = new Intent(getActivity(),currency.class);
                args.putSerializable("ARRAYLIST",(Serializable)currencyList);
                intent.putExtra("BUNDLE",args);
                startActivity(intent);
                break;
            }
            case R.id.EXIT:{
                getActivity().finish();
                System.exit(0);
                break;
            }
            case R.id.LOG:{
                for(int i = 0 ;i<logCal.size();i++){
                    Log.d("#LOGCAL",logCal.get(i));
                }
                Intent intent = new Intent(getActivity(),calculateHistory.class);
                intent.putStringArrayListExtra("LOG",logCal);
                startActivity(intent);
                break;
            }
            case R.id.DATE:{
                Intent intent = new Intent(getActivity(),date.class);
                startActivity(intent);
                break;
            }
            case R.id.LENGTH:{
                Intent intent = new Intent(getActivity(),calculateLength.class);
                startActivity(intent);
                break;
            }
            case R.id.VOLUME:{
                Intent intent = new Intent(getActivity(),calculateVolume.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void initHexAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("原进制");
        final String[] hexList = new String[]{"2","8","10","16"};
        //boolean[] falseList = new boolean[]{false,false,false,false};
        builder.setSingleChoiceItems(hexList, 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fromHEX = Integer.parseInt(hexList[i]);
            }
        });

        //还原(可忽略，因为每次确认都会更新数据
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fromHEX = 10;
                toHEX = 10;
                dialogInterface.dismiss();
            }
        });

        //第一层确认，进入下层AlertDialog
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nextHexAlertDialog();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void nextHexAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("至进制");
        final String[] hexList = new String[]{"2","8","10","16"};
        //boolean[] falseList = new boolean[]{false,false,false,false};
        builder.setSingleChoiceItems(hexList, 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                toHEX = Integer.parseInt(hexList[i]);
            }
        });

        //还原(可忽略，因为每次确认都会更新数据
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fromHEX = 10;
                toHEX = 10;
                dialogInterface.dismiss();
            }
        });

        //第二层确认，结束AlertDialog并进行计算
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(),fromHEX+"|"+toHEX,Toast.LENGTH_SHORT).show();
                calculateHex();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //进制计算
    public void calculateHex(){
        String cur = new String(curInput);
        String left = "",right = "";
        char end = curInput.charAt(curInput.length()-1);
        if(cur.charAt(0) == '('){
            left = "(";
            cur = curInput.substring(1);
        }
        if(end == '+' || end == '-' || end == '*' || end == '/' || end == ')'){
            cur = cur.substring(0,cur.length()-1);
            right += end;
        }
        if(cur.charAt(cur.length()-1) == ')'){
            cur = cur.substring(0,cur.length()-1);
            right = ")" + right;
        }
        //可进行进制转换
        hexFunction hex = new hexFunction();
        try{
            String result = hex.hex(Integer.parseInt(cur),fromHEX,toHEX);
            curInput = left + result + right;
            cur_input.setText(curInput);
        }catch(Exception e){
            Toast.makeText(getActivity(),"请确认数字输入",Toast.LENGTH_SHORT);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("#Fragment1","Fragment1 View Destroyed");
        ((MainActivity)getActivity()).setBundle(saveBundle());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!((MainActivity)getActivity()).getBundle().isEmpty())
            buildBundle();
    }

    public void buildBundle(){
        //rebuild
        Bundle bundle = ((MainActivity)getActivity()).getBundle();
        allInput = bundle.getString("allInput");
        curInput = bundle.getString("curInput");
        infix = bundle.getStringArrayList("infix");
        logCal = bundle.getStringArrayList("logCal");
        pushMark = bundle.getBoolean("pushMark");
        leftBracketCount = bundle.getInt("leftBracketCount");
        rightBracketCount = bundle.getInt("rightBracketCount");
        //refresh
        all_input.setText(allInput);
        cur_input.setText(curInput);
    }


    public Bundle saveBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("allInput",allInput);
        bundle.putString("curInput",curInput);
        bundle.putStringArrayList("infix",infix);
        bundle.putStringArrayList("logCal",logCal);
        bundle.putBoolean("pushMark",pushMark);
        bundle.putInt("leftBracketCount",leftBracketCount);
        bundle.putInt("rightBracketCount",rightBracketCount);
        return bundle;
    }

    protected void init(View view){

        all_input = view.findViewById(R.id.all_input);
        cur_input = view.findViewById(R.id.cur_input);

        btn_1 = view.findViewById(R.id.btn_1);
        btn_2 = view.findViewById(R.id.btn_2);
        btn_3 = view.findViewById(R.id.btn_3);
        btn_4 = view.findViewById(R.id.btn_4);
        btn_5 = view.findViewById(R.id.btn_5);
        btn_6 = view.findViewById(R.id.btn_6);
        btn_7 = view.findViewById(R.id.btn_7);
        btn_8 = view.findViewById(R.id.btn_8);
        btn_9 = view.findViewById(R.id.btn_9);
        btn_0 = view.findViewById(R.id.btn_0);

        btn_left_bracket = view.findViewById(R.id.btn_left_bracket);
        btn_right_bracket = view.findViewById(R.id.btn_right_bracket);

        btn_point = view.findViewById(R.id.btn_point);
        btn_plus = view.findViewById(R.id.btn_plus);
        btn_minus = view.findViewById(R.id.btn_minus);
        btn_multiply = view.findViewById(R.id.btn_multiply);
        btn_divide = view.findViewById(R.id.btn_divide);
        btn_equal = view.findViewById(R.id.btn_equal);
        btn_left_bracket = view.findViewById(R.id.btn_left_bracket);
        btn_right_bracket = view.findViewById(R.id.btn_right_bracket);
        btn_reverse = view.findViewById(R.id.btn_reverse);

        btn_sin = view.findViewById(R.id.btn_sin);
        btn_cos = view.findViewById(R.id.btn_cos);
        btn_tan = view.findViewById(R.id.btn_tan);

        btn_square = view.findViewById(R.id.btn_square);
        btn_sqrt = view.findViewById(R.id.btn_sqrt);

        btn_del = view.findViewById(R.id.btn_del);
        btn_dec = view.findViewById(R.id.btn_dec);

        btn_0.setOnClickListener(new Fragment1.btn0Listener());
        btn_1.setOnClickListener(new Fragment1.btn1Listener());
        btn_2.setOnClickListener(new Fragment1.btn2Listener());
        btn_3.setOnClickListener(new Fragment1.btn3Listener());
        btn_4.setOnClickListener(new Fragment1.btn4Listener());
        btn_5.setOnClickListener(new Fragment1.btn5Listener());
        btn_6.setOnClickListener(new Fragment1.btn6Listener());
        btn_7.setOnClickListener(new Fragment1.btn7Listener());
        btn_8.setOnClickListener(new Fragment1.btn8Listener());
        btn_9.setOnClickListener(new Fragment1.btn9Listener());
        btn_point.setOnClickListener(new Fragment1.btnPointListener());

        btn_right_bracket.setOnClickListener(new Fragment1.btnRightBracket());
        btn_left_bracket.setOnClickListener(new Fragment1.btnLeftBracket());

        btn_sin.setOnClickListener(new btnSinListener());
        btn_cos.setOnClickListener(new btnCosListener());
        btn_tan.setOnClickListener(new btnTanListener());

        btn_square.setOnClickListener(new btnSquareListener());
        btn_sqrt.setOnClickListener(new btnSqrtListener());
        btn_reverse.setOnClickListener(new btnReverseListener());

        btn_plus.setOnClickListener(new Fragment1.btnPlusListener());
        btn_minus.setOnClickListener(new Fragment1.btnMinusListener());
        btn_multiply.setOnClickListener(new Fragment1.btnMultiplyListener());
        btn_divide.setOnClickListener(new Fragment1.btnDivideListener());

        btn_equal.setOnClickListener(new Fragment1.btnEqualListener());

        btn_del.setOnClickListener(new Fragment1.btnDelListener());
        btn_dec.setOnClickListener(new Fragment1.btnDecListener());
        cur_input.setText(curInput);
    }
    //监听运算按钮
    class btnEqualListener implements  View.OnClickListener{
        @Override
        public void onClick(View view) {
            try{
                charCheck();
                String temp = new String((allInput + curInput));
                if(curInput.charAt(curInput.length()-1) == ')'){
                    infix.add(curInput.substring(0,curInput.length()-1));
                    infix.add(")");
                }
                else
                    infix.add(curInput);
                for(int i = 0;i<infix.size();i++){
                    Log.d("#LISTCHECK",infix.get(i));
                }

                //中缀表达式->后缀表达式
                swap();
                Double result = suffixCalculate();
                Log.d(TAG,"cal result:"+result.toString());
                //刷新 同dec
                refresh();
                all_input.setText(temp);
                //显示结果处理,最长保留6位小数，之后改为可设置位数
                curInput = String.format("%.6f",result);
                formatCurInput();
                cur_input.setText(curInput);
                temp += "="+curInput;
                logCal.add(temp);
            }catch(Exception e){
                Toast.makeText(getActivity(),"算式错误",Toast.LENGTH_SHORT).show();
                refresh();
            }
        }

        public Double suffixCalculate(){
            Stack<Double> cs = new Stack<>();
            for(int i = 0;i<suffix.size();i++){
                String cur = suffix.get(i);
                if(cur.equals("+") || cur.equals("-") ||cur.equals("*") || cur.equals("/")){
                    double y = cs.pop();
                    double x = cs.pop();
                    cs.push(ezCalculate(x,y,suffix.get(i)));
                }
                else{
                    cs.push(Double.parseDouble(suffix.get(i)));
                }
            }
            return cs.pop();
        }

        public Double ezCalculate(double x,double y,String operator){
            if(operator.equals("+"))
                return x + y;
            else if(operator.equals("-"))
                return x - y;
            else if(operator.equals("*"))
                return x * y;
            else if(operator.equals("/"))
                return x / y;
            return 0.0;
        }

        public void swap(){
            for(int i = 0;i<infix.size();i++){
                String t = infix.get(i);
                switch (t){
                    case "(":{
                        calStack.push("(");
                        break;
                    }
                    case ")":{
                        while(calStack.size()!=0){
                            String c = calStack.pop();
                            if(c.equals("("))
                                break;
                            else
                                suffix.add(c);
                        }
                        break;
                    }
                    case "*":
                    case "/":
                        while(calStack.size() != 0){
                            String c = calStack.pop();
                            if(c.equals("(") || c.equals("+") || c.equals("-")){
                                calStack.push(c);
                                break;
                            }
                            else
                                suffix.add(c);
                        }
                        calStack.push(t);
                        break;

                    case "+":
                    case "-":
                        while(calStack.size()!=0){
                            String c = calStack.pop();
                            if(c.equals("(")){
                                calStack.push("(");
                                break;
                            }
                            suffix.add(c);
                        }
                        calStack.push(t);
                        break;

                    default:{
                        suffix.add(t);
                        break;
                    }
                }
            }
            while(calStack.size()!=0){
                suffix.add(calStack.pop());
            }
            for(int i = 0;i<suffix.size();i++){
                Log.d("#SUFFIX",suffix.get(i));
            }
        }
    }
    //监听即时运算按钮
    class btnSinListener implements  View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(numberCheck()){
                Double num = Double.parseDouble(curInput);
                Log.d(TAG,"num:"+num);
                Double result = Math.sin(Math.toRadians(num));
                curInput = String.format("%.6f",result);
                formatCurInput();
                cur_input.setText(curInput);
            }
        }
    }
    class btnCosListener implements  View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(numberCheck()){
                Double num = Double.parseDouble(curInput);
                Log.d(TAG,"num:"+num);
                Double result = Math.cos(Math.toRadians(num));
                curInput = String.format("%.6f",result);
                formatCurInput();
                cur_input.setText(curInput);
            }
        }
    }
    class btnTanListener implements  View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(numberCheck()){
                Double num = Double.parseDouble(curInput);
                if((num%360) == 90 || (num%360) == -90){
                    Toast.makeText(getActivity(), "无穷", Toast.LENGTH_SHORT).show();
                }
                else {
                    Double result = Math.tan(Math.toRadians(num));
                    curInput = String.format("%.6f", result);
                    formatCurInput();
                    cur_input.setText(curInput);
                }
            }
        }
    }
    class btnSquareListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Log.d(TAG,"square");
            char start = curInput.charAt(0),end = curInput.charAt(curInput.length()-1);
            if(isSymbol(end)){
                Toast.makeText(getActivity(),"请校正输入",Toast.LENGTH_SHORT).show();
            }
            else{
                String num = "";
                if(start == '(')
                    num = curInput.substring(1);
                else
                    num = curInput;
                Double result = Double.parseDouble(num) * Double.parseDouble(num);
                curInput = String.format("%.6f",result);
                formatCurInput();
                cur_input.setText(curInput);
            }
        }
    }
    class btnSqrtListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Log.d(TAG,"sqrt");
            char start = curInput.charAt(0),end = curInput.charAt(curInput.length()-1);
            if(isSymbol(end)){
                Toast.makeText(getActivity(),"请校正输入",Toast.LENGTH_SHORT);
            }
            else{
                String num = "";
                if(start == '(')
                    num = curInput.substring(1);
                else
                    num = curInput;
                Double result = Math.sqrt(Double.parseDouble(num));
                curInput = String.format("%.6f",result);
                formatCurInput();
                cur_input.setText(curInput);
            }
        }
    }
    class btnReverseListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //rev 0 = 0
            if(curInput.equals("0")){
                return;
            }
            char end = curInput.charAt(curInput.length()-1);
            //数字输入错误筛选
            if(end == '+' || end == '-' || end == '*' || end == '/' || end == '.' || end == ')' || end == '('){
                return;
            }
            //左侧为括号
            else if(curInput.charAt(0) == '('){
                String reversedStr = "(";
                if(curInput.charAt(1) == '-')
                    reversedStr += curInput.substring(2);
                else
                    reversedStr = reversedStr + "-" + curInput.substring(1);
                curInput = reversedStr;
            }
            //纯数字
            else {
                if(curInput.charAt(0) == '-')
                    curInput = curInput.substring(1);
                else
                    curInput = "-" + curInput;
            }
            cur_input.setText(curInput);
        }
    }
    //监听其他按钮
    class btnRightBracket implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(rightBracketCount < leftBracketCount){
                char end = curInput.charAt(curInput.length()-1);
                if(end =='+' || end == '-' || end == '*' || end=='/' || end == '.'){
                    curInput = curInput.substring(0,curInput.length()-1);
                    curInput += ")";
                    rightBracketCount += 1;
                }
                else if(end == '('){
                    leftBracketCount -= 1;
                    curInput = "0";
                }
                else
                {
                    curInput += ")";
                    rightBracketCount += 1;
                }
                cur_input.setText(curInput);
            }

        }
    }
    class btnLeftBracket implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(allInput.equals("") && curInput.equals("0")){
                curInput = "(";
                cur_input.setText(curInput);
                leftBracketCount += 1;
            }
            else if(pushMark) {
                pushInput();
                curInput = "(";
                cur_input.setText(curInput);
                leftBracketCount += 1;
            }
            else
                return;
        }
    }
    class btn0Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Log.d(TAG,"btn_0 clicked");
            if(pushMark){
                pushInput();
            }
            if(curInput.equals("0")){
                //do nothing
            }
            else{
                curInput += "0";
            }
            cur_input.setText(curInput);

            Log.d(TAG,curInput);
        }
    }
    class btn1Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(pushMark){
                pushInput();
            }
            if(curInput.equals("0")){
                curInput = "1";
            }
            else
                curInput += "1";
            cur_input.setText(curInput);

            Log.d(TAG,curInput);

        }
    }
    class btn2Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(pushMark){
                pushInput();
            }
            if(curInput.equals("0")){
                curInput = "2";
            }
            else
                curInput += "2";
            cur_input.setText(curInput);

            Log.d(TAG,curInput);
        }
    }
    class btn3Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(pushMark){
                pushInput();
            }
            if(curInput.equals("0")){
                curInput = "3";
            }
            else
                curInput += "3";
            cur_input.setText(curInput);

            Log.d(TAG,curInput);
        }
    }
    class btn4Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(pushMark){
                pushInput();
            }
            if(curInput.equals("0")){
                curInput = "4";
            }
            else
                curInput += "4";
            cur_input.setText(curInput);

            Log.d(TAG,curInput);
        }
    }
    class btn5Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(pushMark){
                pushInput();
            }
            if(curInput.equals("0")){
                curInput = "5";
            }
            else
                curInput += "5";

            cur_input.setText(curInput);

            Log.d(TAG,curInput);
        }
    }
    class btn6Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(pushMark){
                pushInput();
            }
            if(curInput.equals("0")){
                curInput = "6";
            }
            else
                curInput += "6";
            cur_input.setText(curInput);

            Log.d(TAG,curInput);
        }
    }
    class btn7Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(pushMark){
                pushInput();
            }
            else{
                if(curInput.equals("0")){
                    curInput = "7";
                }
                else
                    curInput += "7";
            }
            cur_input.setText(curInput);

            Log.d(TAG,curInput);
        }
    }
    class btn8Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(pushMark){
                pushInput();
            }
            if(curInput.equals("0")){
                curInput = "8";
            }
            else
                curInput += "8";
            cur_input.setText(curInput);

            Log.d(TAG,curInput);
        }
    }
    class btn9Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(pushMark){
                pushInput();
            }
            if(curInput.equals("0")){
                curInput = "9";
            }
            else
                curInput += "9";
            cur_input.setText(curInput);

            Log.d(TAG,curInput);
        }
    }
    class btnPointListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int i;
            char s = curInput.charAt(curInput.length()-1);
            for(i = 0 ;i < curInput.length();i++){
                if(curInput.charAt(i) == '.') {
                    break;
                }
            }
            if(s=='+'||s=='-'||s=='*'||s=='/'||s=='('||s==')'){

            }
            else if(i == curInput.length()){
                curInput += ".";
            }
            else{

            }
            cur_input.setText(curInput);
            Log.d(TAG,curInput);
        }
    }
    class btnDelListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //停止推入上层
            pushMark = false;
            if(curInput.length() == 1){
                curInput = "0";
            }
            else{
                curInput = curInput.substring(0,curInput.length()-1);
            }
            cur_input.setText(curInput);
        }
    }
    class btnDecListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            refresh();
        }
    }
    class btnPlusListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Log.d(TAG,"+");
            charCheck();
            curInput += "+";
            cur_input.setText(curInput);
            Log.d(TAG,curInput);
            pushMark = true;
        }
    }
    class btnMinusListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //分情况讨论
            //1.作为减号，在curInput的末尾，pushMark = true
            //2.curInput.equals("0"),curInput = "-",pushMark = false
            //3.curInput.charAt(0) == '(',curInput += "-",pushMark = false

            //情况3
            if(curInput.charAt(0) == '('){
                curInput += "-";
                pushMark = false;
            }
            //情况2
            else if(curInput.equals("0")){
                curInput = "-";
                pushMark = false;
            }
            //情况1
            else{
                charCheck();
                curInput += "-";
                pushMark = true;
            }
            cur_input.setText(curInput);
        }
    }
    class btnMultiplyListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Log.d(TAG,"*");
            charCheck();
            curInput += "*";
            cur_input.setText(curInput);
            Log.d(TAG,curInput);
            pushMark = true;
        }
    }
    class btnDivideListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Log.d(TAG,"/");
            charCheck();
            curInput += "/";
            cur_input.setText(curInput);
            pushMark = true;
        }
    }
    //功能
    public void refresh(){
        pushMark = false;
        curInput = "0";
        allInput = "";
        cur_input.setText(curInput);
        all_input.setText(allInput);
        infix = new ArrayList<String>();
        suffix = new ArrayList<String>();
        calStack = new Stack<String>();
        leftBracketCount = 0;
        rightBracketCount = 0;
    }
    public void charCheck(){
        char c = curInput.charAt(curInput.length()-1);
        Log.d(TAG,curInput);
        if(c == '+' || c == '-' || c == '*' || c == '/' || c == '.')
            curInput = curInput.substring(0,curInput.length() - 1);
    }
    public void pushInput(){
        allInput = allInput == null?curInput:allInput+curInput;
        all_input.setText(allInput);
        //左右括号处理
        if(curInput.charAt(0) == '('){
            String leftBracket = "(";
            infix.add(leftBracket);
            curInput = curInput.substring(1);
            leftBracketCount += 1;
        }

        //...num)=
        if(curInput.charAt(curInput.length()-1) == ')'){
            String num = curInput.substring(0,curInput.length()-1);
            infix.add(num);
            infix.add(")");
            rightBracketCount += 1;
        }
        //...num)*
        else if(curInput.charAt(curInput.length()-2) == ')'){
            String num = curInput.substring(0,curInput.length()-2);
            String ope = curInput.substring(curInput.length()-1);
            infix.add(num);
            infix.add(")");
            infix.add(ope);
            rightBracketCount +=1;
        }
        //..num+
        else{
            String num = curInput.substring(0,curInput.length()-1);
            String ope = curInput.substring(curInput.length()-1);
            infix.add(num);
            infix.add(ope);
        }
        //数字与操作符处理
        curInput = "0";
        pushMark = !pushMark;
    }
    public boolean numberCheck(){
        //无需从头至尾检查，只需检查当前输入头尾
        //输入过程中小数点控制已在监听小数点按钮事件进行处理
        //括号检查
        char endChar = curInput.charAt(curInput.length()-1);
        if(curInput.charAt(0) == '(' || endChar == ')'){
            Toast.makeText(getActivity(),"括号阻止了此次操作",Toast.LENGTH_SHORT).show();
            return false;
        }
        //符号检查
        else if(endChar == '+' || endChar == '-' || endChar == '*' || endChar =='/'){
            Toast.makeText(getActivity(),"运算符号阻止了此次操作",Toast.LENGTH_SHORT).show();
            return false;
        }
        //末尾小数点，处理掉后允许执行操作
        else if(endChar == '.'){
            curInput = curInput.substring(curInput.length()-1);
        }
        //标准数字
        else{

        }
        return true;
    }
    public boolean isSymbol(char c){
        if(c == '+' || c == '-' || c == '*' || c == '/' || c == '.' || c == ')' || c == '('){
            return true;
        }
        return false;
    }
    public void formatCurInput(){
        int formatPoint;
        for(formatPoint = curInput.length()-1;formatPoint>=0;formatPoint--){
            if(curInput.charAt(formatPoint) != '0')
                break;
        }
        if(curInput.charAt(formatPoint) == '.')
            formatPoint -= 1;
        curInput = curInput.substring(0,formatPoint+1);
    }


    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String fBuyPri = jsonObject.getString("fBuyPri");
                String fSellPri = jsonObject.getString("fSellPri");

                //float p = Float.parseFloat(fBuyPri);

                currencyItem c = new currencyItem();
                c.setName(name);
                c.setfBuyPri(Float.parseFloat(fBuyPri));
                c.setfSellPri(Float.parseFloat(fSellPri));
                currencyList.add(c);
                Log.d("#JSON",c.getName());
                Log.d("#JSON",Float.toString(c.getfBuyPri()));
                Log.d("#JSON",Float.toString(c.getfSellPri()));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://bzpnb.xyz:7890/androidRequest")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithJSONObject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
