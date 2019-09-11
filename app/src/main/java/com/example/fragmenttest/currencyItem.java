package com.example.fragmenttest;

import java.io.Serializable;

public class currencyItem implements Serializable {
    private String name;
    private float fBuyPri;
    private float fSellPri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getfBuyPri() {
        return fBuyPri;
    }

    public void setfBuyPri(float fBuyPri) {
        this.fBuyPri = fBuyPri;
    }

    public float getfSellPri() {
        return fSellPri;
    }

    public void setfSellPri(float fSellPri) {
        this.fSellPri = fSellPri;
    }
}
