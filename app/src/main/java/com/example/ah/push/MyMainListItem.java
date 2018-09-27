package com.example.ah.push;

public class MyMainListItem {
    // Store the id of the  movie poster
    private int mImageDrawable;
    // Store the name of the movie
    private String mName;

    // Constructor that is used to create an instance of the Movie object
    public MyMainListItem(int mImageDrawable, String mName) {
        this.mImageDrawable = mImageDrawable;
        this.mName = mName;
    }

    public int getmImageDrawable() {
        return mImageDrawable;
    }

    public void setmImageDrawable(int mImageDrawable) {
        this.mImageDrawable = mImageDrawable;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

}