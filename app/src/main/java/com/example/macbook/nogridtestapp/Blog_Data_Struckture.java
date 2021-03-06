package com.example.macbook.nogridtestapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by macbook on 6/25/15.
 */
public class Blog_Data_Struckture implements Parcelable {


    private String blogName;
    private String blogDescription;
    private String rssFeedURL;
    private ArrayList<Rss_Data_Struckture> rssNewsList;
    private String blogImage;
    private boolean everyThingAlright;

    public Blog_Data_Struckture(String blogName, String rssFeed , String blogDescription, String image) {

        this.blogName = blogName;
        this.rssFeedURL = rssFeed;
        this.blogDescription = blogDescription;
        this.blogImage = image;
        this.rssNewsList = null;

        checkRSSLink();

    }


    public String getBlogName() {
        return blogName;
    }

    public String getRssFeedURL() {
        return rssFeedURL;
    }

    public String getBlogimage() {
        return blogImage;
    }

    public ArrayList<Rss_Data_Struckture> getRssNewsList() {
        return rssNewsList;
    }

    public boolean isEveryThingAlright() {
        return everyThingAlright;
    }

    public void setEveryThingAlright(boolean everyThingAlright) {
        this.everyThingAlright = everyThingAlright;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    private void setEveryThingsAlright(boolean everyThingsAlright) {
        this.everyThingAlright = everyThingsAlright;
    }

    public void setBlogImage(String blogImage) {
        this.blogImage = blogImage;
    }

    public void setRssNewsList(ArrayList<Rss_Data_Struckture> rssNewsList) {
        this.rssNewsList = rssNewsList;
    }


    public void checkRSSLink() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                URL url = null;
                try {
                    url = new URL(getRssFeedURL());

                } catch (MalformedURLException e) {
                    everyThingAlright = false;
                    //e.printStackTrace();
                }

                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                    inputStream.close();
                    httpURLConnection.disconnect();
                    everyThingAlright = true;

                } catch (IOException e) {

                    everyThingAlright = false;
                    //e.printStackTrace();
                } catch(NullPointerException e){
                    everyThingAlright = false;

                }

            }
        });

        thread.start();

    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(blogName);
        dest.writeString(rssFeedURL);
        //dest.writeParcelableArray(ArrayList < Rss_Data_Struckture > rssNewsList, flags);
        dest.writeString(blogImage);
        if (everyThingAlright == true) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }

    }


    public Blog_Data_Struckture(Parcel source) {
        this.blogName = source.readString();
        this.rssFeedURL = source.readString();
        this.blogImage = source.readString();
        int bool = source.readInt();
        if (bool==1){
            this.everyThingAlright=true;
        }
        else {
            this.everyThingAlright=false;
        }
        //source.readBooleanArray(rssNewsList[0]);


    }

    public String getBlogDescription() {
        return blogDescription;
    }

    public void setBlogDescription(String blogDescription) {
        this.blogDescription = blogDescription;
    }
}