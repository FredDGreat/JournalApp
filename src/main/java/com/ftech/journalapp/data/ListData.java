package com.ftech.journalapp.data;

/**
 * Created by Frederick on 5/19/2018.
 */

public class ListData {
    String mTitle,mDesc,mLetter,mDate,mLetterColor;
    int id;
    public ListData(){
    }
    public ListData(String title,String desc,String letter,String date,String letterColor){
        mTitle = title;
        mDesc = desc;
        mLetter = letter;
        mDate = date;
        mLetterColor = letterColor;
    }

    public void setmLetterColor(String mLetterColor) {
        this.mLetterColor = mLetterColor;
    }

    public String getmLetterColor() {
        return mLetterColor;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDesc() {
        return mDesc;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmLetter(String mLetter) {
        this.mLetter = mLetter;
    }

    public String getmLetter() {
        return mLetter;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return mTitle+"\n"+mDesc+"\n"+mLetter+"\n"+mDate;
    }
}
