package com.example.macbook.nogridtestapp.async_classes;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.macbook.nogridtestapp.my_interface.Add_Dialog_Interface;
import com.example.macbook.nogridtestapp.my_interface.Async_URLTest_Interface;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by macbook on 2015-08-26.
 */
public class Async_URLTest extends AsyncTask<String,Void,Bundle>{

    public Async_URLTest_Interface return_interface;
    Bundle bundle = new Bundle();
    Context context;

    public Async_URLTest(Context context){
        this.context = context;

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bundle doInBackground(String... params) {

        Log.d("doInBackground","#######got called");

        String url = params[0];

/*
        //falls http fehlt
        if(url.substring(3) != "http"){
            url_test("http://"+url,params[1]);


            if(bundle.getBoolean("connect") == false){
            url_test("https://"+url,params[1]);

            }

        }
        else{
            url_test(url,params[1]);

        }
*/
        url_test(url,params[1]);

        return bundle;
    }

    private boolean url_test(String string_url,String typed_name){

        String name = "";
        Boolean connect = false;
        String title_image = "";
        String description = "-";

        try {

            URL url = new URL(string_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream,null);

            int eventType = parser.getEventType();
            String eventName = "";


            while ((eventType != XmlPullParser.END_DOCUMENT) && !(eventName != null && (eventName.equals("item") || (eventName.equals("entry"))))){

                Log.d("doInBackground", "#######first while");

                eventType=parser.getEventType();
                eventName=parser.getName();

                if(eventType == XmlPullParser.START_TAG && (eventName != null && (eventName.equals("title") && name == ""))){
                    //name!="" damit nicht versehendlich ein falscher titel gespeichert wird

                    parser.next();
                    Log.d("doInBackground", "#######title " + parser.getText());
                    name = parser.getText();
                }

                if(eventType == XmlPullParser.START_TAG && (eventName != null && (eventName.equals("description")))){

                    parser.next();
                    Log.d("doInBackground", "#######first description " + parser.getText());
                    description = parser.getText().replace("\n"," ");
                }

                if (eventType == XmlPullParser.START_TAG && (eventName != null && ((eventName.equals("url") || (eventName.equals("icon")))))) {

                    parser.next();
                    Log.d("doInBackground", "#######url");
                    title_image = parser.getText();

                    Async_Thumbnails thumbnails = new Async_Thumbnails(context);

                    //wichtig für den namen des thmbnails, falls der user sein eigennen namen für den blog eingibt, wird dieser für den
                    //thumbnail benutzt

                    if(typed_name != "" || typed_name != "Name") {
                        thumbnails.execute(typed_name,title_image);
                    }
                    else if(typed_name == "" || typed_name == "Name" || name == "") {
                        thumbnails.execute("rss_feed",title_image);
                    }
                    else {
                        thumbnails.execute(name,title_image);
                    }

                }

                else{
                    parser.next();
                }
            }

            connect = true;
            inputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            connect = false;
        } catch (IOException e) {
            e.printStackTrace();
            connect = false;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            connect = false;
        } catch (NullPointerException e){
            connect = false;
            e.printStackTrace();
        }


        bundle.putString("url",string_url);
        name = name;
        bundle.putString("name", name);
        bundle.putString("description", description);
        bundle.putString("image", title_image);
        bundle.putBoolean("connect", connect);
        System.out.println(bundle.getString("name"));
        return true;
    }

    @Override
    protected void onPostExecute(Bundle bundle) {

        Log.d("onPostExecute", "#######got called");

        Log.d("onPostExecute","#######got called");

        //System.out.println(String.valueOf(bundle.getBoolean("connect")+));

        return_interface.url_test_result(bundle);
    }
}
