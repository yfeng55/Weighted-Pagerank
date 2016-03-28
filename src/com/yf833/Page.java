package com.yf833;


import java.util.ArrayList;

public class Page {

    public String title;
    public int wordcount;
    public float base;
    public float score;
    public float newscore;
    public ArrayList<String> outlinks;


    public Page(String title){

        this.title = title;

        this.base = 0;
        this.score = 0;
        this.newscore = 0;
        this.outlinks = new ArrayList<>();

    }

    public String toString(){
        return this.title + ":" + this.outlinks.toString() + this.wordcount;
    }



}
