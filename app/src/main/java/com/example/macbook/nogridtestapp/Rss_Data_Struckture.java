package com.example.macbook.nogridtestapp;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by macbook on 6/23/15.
 *
 *
 *
 * Rss-Data Klasse
 *
 *
 *
 */
public class Rss_Data_Struckture implements Parcelable {

    private String rssTitle;
    private String rssLink;
    private String rssDescription;
    private String rssPubDate;
    private String rssImage;

    public Rss_Data_Struckture(String rssHeadline, String rsslink) {

        this.rssTitle = rssHeadline;
        this.rssLink = rsslink;
        this.rssDescription = "";
        this.rssPubDate = "";
        this.rssImage = null;
        //Log.d("Rss_data", "###############construktor wurde aufgerufen");

    }

    public void setrssTitle(String rssTitle) {
        this.rssTitle = rssTitle;
    }

    public void setrssLink(String rsslink) {
        this.rssLink= rsslink;
    }

    public void setRssdescription(String rssdescription) {
        this.rssDescription = rssdescription;
    }

    public void setRssPubDate(String rssPubDate) {
        this.rssPubDate = rssPubDate;
    }

    public void setImage(String  image) {
        this.rssImage = image;
    }

    public String getRssTitle() {
        return rssTitle;
    }

    public String getRssLink() {
        return rssLink;
    }

    public String getRssPubDate() {
        return rssPubDate;
    }

    public String getRssDescription() {
        return rssDescription;
    }

    public String getImage() {
        return rssImage;
    }


    //importent for pasring this object
    @Override
    public int describeContents() {
        hashCode();
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(rssTitle);
        dest.writeString(rssLink);
        dest.writeString(rssDescription);
        dest.writeString(rssPubDate);
        dest.writeString(rssImage);
    }

    public Rss_Data_Struckture(Parcel source) {

        //Log.d("Parcelable","got parceled?!");

        rssTitle = source.readString();
        rssLink = source.readString();
        rssDescription = source.readString();
        rssPubDate = source.readString();
        rssImage = source.readString();
        //rssImage = (Bitmap) source.readValue(Bitmap.class.getClassLoader()); //als die bilder noch gespeichert werden sollten
    }

    public static final Parcelable.Creator<Rss_Data_Struckture> CREATOR = new Parcelable.Creator<Rss_Data_Struckture>() {
        @Override
        public Rss_Data_Struckture createFromParcel(Parcel source) {
            return new Rss_Data_Struckture(source);
        }

        @Override
        public Rss_Data_Struckture[] newArray(int size) {
            return new Rss_Data_Struckture[size];
        }
    };
}


