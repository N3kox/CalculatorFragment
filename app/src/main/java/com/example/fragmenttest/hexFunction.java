package com.example.fragmenttest;

import java.util.HashMap;

public class hexFunction {
    private HashMap<String, String> hexList = new HashMap<>();
    private HashMap<String, String> revHexList = new HashMap<String, String>();

    public hexFunction() {
        hexListInit();
    }

    private void hexListInit() {
        String[] keyList = new String[]{"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String[] valueList = new String[] {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
        for(int i = 0;i<keyList.length;i++) {
            hexList.put(keyList[i],valueList[i]);
            revHexList.put(valueList[i],keyList[i]);
        }
    }


    //暴露 总hex方法
    public String hex(int currentInput,int fromHex,int toHex) {
        String result = String.valueOf(currentInput);
        if(fromHex != 10) {
            result = partA(result,fromHex);
        }
        if(toHex != 10 && !result.equals("error")) {
            return partB(result,toHex);
        }
        return result;
    }


    //fromHex进制转为10进制
    //如果输入的数字超过进制读取范围，返回String error
    private String partA(String input,int fromHex) {
        int basic = 1;
        int result = 0;
        for(int i=input.length()-1;i>=0;i--) {
            String s = input.substring(i,i+1);
            int index = Integer.parseInt(hexList.get(s).toString());
            if(index >= fromHex) {
                return "error";
            }
            else {
                result += basic * (Integer.parseInt(hexList.get(s).toString()));
                basic *= fromHex;
            }
        }
        return String.valueOf(result);
    }

    //10进制转为toHex进制
    private String partB(String input,int toHex) {
        int temp = Integer.parseInt(input);
        String pushResult = "";
        while(temp != 0) {
            String reg = String.valueOf(temp % toHex);
            pushResult += revHexList.get(reg);
            temp = temp / toHex;
        }
        String result = new StringBuffer(pushResult).reverse().toString();
        System.out.println("result"+result);
        return result;
    }
}
