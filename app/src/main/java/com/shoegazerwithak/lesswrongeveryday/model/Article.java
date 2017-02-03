package com.shoegazerwithak.lesswrongeveryday.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * POJO to save data from JSON
 */

public class Article implements Parcelable {

    /**
     * Parcelable stuff
     **/

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
//    public int id;
    public String title;
    public String link;
    public String text;

    public Article(String mTitle, String mLink) {
        title = mTitle;
        link = mLink;
    }

    public Article(String mTitle, String mLink, String mText) {
        title = mTitle;
        link = mLink;
        text = mText;
    }

    public Article(Parcel in) {
//        id = in.readInt();
        title = in.readString();
        link = in.readString();
        text = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(link);
        parcel.writeString(text);
    }
}
