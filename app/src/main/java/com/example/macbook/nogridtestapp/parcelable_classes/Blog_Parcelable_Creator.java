package com.example.macbook.nogridtestapp.parcelable_classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.macbook.nogridtestapp.Blog_Data_Struckture;

/**
 * Created by macbook on 2015-06-26.
 */
public class Blog_Parcelable_Creator implements Parcelable.Creator<Blog_Data_Struckture> {
    @Override
    public Blog_Data_Struckture createFromParcel(Parcel source) {
        return new Blog_Data_Struckture(source);
    }

    @Override
    public Blog_Data_Struckture[] newArray(int size) {
        return new Blog_Data_Struckture[size];
    }
}
