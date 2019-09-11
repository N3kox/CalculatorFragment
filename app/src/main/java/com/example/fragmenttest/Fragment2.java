package com.example.fragmenttest;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {

    final String TAG= "#Main";
    EditText all_input,cur_input;
    Button btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9,btn_0;
    Button btn_left_bracket,btn_right_bracket;
    Button btn_point,btn_plus,btn_minus,btn_multiply,btn_divide,btn_equal;
    Button btn_del,btn_dec;
    String allInput="",curInput = "0";
    ArrayList<String> infix = new ArrayList<String>();
    ArrayList<String> suffix = new ArrayList<String>();
    Stack<String> calStack = new Stack<>();
    boolean pushMark = false;
    int leftBracketCount = 0,rightBracketCount = 0;
    ArrayList<String> logCal = new ArrayList<>();

    public Fragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);
        Log.d("#Fragment2","Fragment2 init");
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!((MainActivity)getActivity()).getBundle().isEmpty())
            buildBundle();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("#Fragment2","Fragment2 View Destroyed");
        ((MainActivity)getActivity()).setBundle(saveBundle());
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

        btn_del = view.findViewById(R.id.btn_del);
        btn_dec = view.findViewById(R.id.btn_dec);

        btn_0.setOnClickListener(new btn0Listener());
        btn_1.setOnClickListener(new btn1Listener());
        btn_2.setOnClickListener(new btn2Listener());
        btn_3.setOnClickListener(new btn3Listener());
        btn_4.setOnClickListener(new btn4Listener());
        btn_5.setOnClickListener(new btn5Listener());
        btn_6.setOnClickListener(new btn6Listener());
        btn_7.setOnClickListener(new btn7Listener());
        btn_8.setOnClickListener(new btn8Listener());
        btn_9.setOnClickListener(new btn9Listener());
        btn_point.setOnClickListener(new btnPointListener());

        btn_right_bracket.setOnClickListener(new btnRightBracket());
        btn_left_bracket.setOnClickListener(new btnLeftBracket());

        btn_plus.setOnClickListener(new btnPlusListener());
        btn_minus.setOnClickListener(new btnMinusListener());
        btn_multiply.setOnClickListener(new btnMultiplyListener());
        btn_divide.setOnClickListener(new btnDivideListener());

        btn_equal.setOnClickListener(new btnEqualListener());

        btn_del.setOnClickListener(new btnDelListener());
        btn_dec.setOnClickListener(new btnDecListener());
        cur_input.setText(curInput);
    }
    //监听运算按钮
    class btnEqualListener implements  View.OnClickListener{
        @Override
        public void onClick(View view) {
            try{
                charCheck();
                String t = allInput + curInput;
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

                //显示结果处理,最长保留6位小数，之后改为可设置位数
                String temp = String.format("%.6f",result);
                int formatPoint;
                for(formatPoint = temp.length()-1;formatPoint>=0;formatPoint--){
                    if(temp.charAt(formatPoint) != '0')
                        break;
                }
                if(temp.charAt(formatPoint) == '.')
                    formatPoint -= 1;
                curInput = temp.substring(0,formatPoint+1);
                cur_input.setText(curInput);
                t += "="+curInput;
                logCal.add(t);
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
    //监听其他按钮
    class btnRightBracket implements View.OnClickListener{
        @Override
        public void onClick(View view) {
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
}
