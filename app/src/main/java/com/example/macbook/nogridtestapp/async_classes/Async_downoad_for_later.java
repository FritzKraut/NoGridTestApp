package com.example.macbook.nogridtestapp.async_classes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by macbook on 9/6/15.
 */
public class Async_downoad_for_later extends AsyncTask<ArrayList<String>,Integer,Boolean> {

    private Context context;
    private String name;
    private String rss_url;
    private final WeakReference<MenuItem> mProgress;
    private int mProgressStatus = 0;

    public Async_downoad_for_later(Context context,MenuItem mProgress){
        this.context = context;
        this.mProgress = new WeakReference<MenuItem>(mProgress);

    }

    @Override
    protected void onPreExecute() {
        //falls keine refferenz übergeben wird, beispiel, einfacher blog aufruf
        if(!(mProgress.get() == null)){;
            //mProgress.get().setProgress(0);

        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //mProgress.get().setProgress((int)((double)(values[0]*100/mProgressStatus)));
    }

    @Override
    protected Boolean doInBackground(ArrayList<String>... params) {
        Log.d("Async_downoad_for_later", "gets called");

        ArrayList <String> array = params[0];
        URL url;
        FileOutputStream outputStream = null;
        HttpURLConnection httpURLConnection;
        InputStream inputStream = null;
        mProgressStatus = array.size();



        for (int i = 0; i < mProgressStatus;i+=2){

            if (!(mProgress.get() == null)){
                //funktioniert nicht mehr?????
                //onProgressUpdate(i+1);
            }

            name = array.get(i);
            rss_url = array.get(i+1);

            try {
                Log.d("Async_downoad_for_later", "loading url: " + name);

                url = new URL(rss_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                outputStream = context.openFileOutput(name + "_save.xml",context.MODE_PRIVATE);

                byte data[] = new byte[8096];
                long total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1){
                    total += count;
                    outputStream.write(data,0,count);
                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    outputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {

        if(!(mProgress.get() == null)){
            Toast.makeText(context, "SAVED XML FOR LATER", Toast.LENGTH_SHORT).show();

            //mProgress.get().setVisibility(View.GONE);

            mProgress.get().collapseActionView();
            mProgress.get().setActionView(null);

        }
    }
}
