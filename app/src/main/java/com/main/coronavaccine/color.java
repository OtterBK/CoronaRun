package com.main.coronavaccine;

import android.graphics.Color;

public class color {
    public static final int[] colors = new int[100];
    private int r=0,g=0,b=0;
    public color(){
        super();
    }
    public void setColor(int n){
        for(int i=0;i<n;i++){
            r= (int)Math.floor(Math.random()*255);
            g= (int)Math.floor(Math.random()*255);
            b= (int)Math.floor(Math.random()*255);
            colors[i]=Color.rgb(r,g,b);
        }
    }
}
