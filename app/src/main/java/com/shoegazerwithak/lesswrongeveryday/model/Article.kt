package com.shoegazerwithak.lesswrongeveryday.model

import android.os.Parcel
import android.os.Parcelable

/**
 * POJO to save data from JSON
 */

class Article : Parcelable {
    //    public int id;
    var title: String
    var link: String
    var text: String = ""

    constructor(mTitle: String, mLink: String, mText: String = "") {
        title = mTitle
        link = mLink
        text = mText
    }

    constructor(`in`: Parcel) {
        //        id = in.readInt();
        title = `in`.readString()
        link = `in`.readString()
        text = `in`.readString()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, i: Int) {
        //        parcel.writeInt(id);
        parcel.writeString(title)
        parcel.writeString(link)
        parcel.writeString(text)
    }

    companion object {
        /**
         * Parcelable stuff
         */
        val CREATOR: Parcelable.Creator<Article> = object : Parcelable.Creator<Article> {
            override fun createFromParcel(`in`: Parcel): Article {
                return Article(`in`)
            }

            override fun newArray(size: Int): Array<Article?> = arrayOfNulls(size)
        }
    }
}