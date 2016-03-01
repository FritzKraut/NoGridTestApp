package com.example.macbook.nogridtestapp.async_classes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.macbook.nogridtestapp.my_interface.AsyncResponse;
import com.example.macbook.nogridtestapp.Rss_Data_Struckture;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
* this class downlads feed from url stores an the phones storage and in a Rss_Data object to returns it
*
* */

public class AsyncTaskURL extends AsyncTask<String, Void, Bundle> {


    public AsyncResponse myInterface = null;
    boolean finDownloading = false;
    private int a;
    private String blogname;
    private String blogurl;
    private Context context;
    private boolean toast = false;

    private ArrayList<Rss_Data_Struckture> news = new ArrayList<Rss_Data_Struckture>();

    public AsyncTaskURL(Context context) {
        this.context = context;


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    /**
     *  opens url and creates XmlPullParser
     *  uses get_rss function
     *  and download_rss function
     */

    protected Bundle doInBackground(String... params) {

        Bundle bundle = new Bundle();
        blogname = params[1];
        blogurl = params[0];


        Log.d("doInBackground", "########doInBackground got called");


        try {

            //bekommt url, öffnet connection, wandeslt es in einen inputstream um, returnt es
            URL url = new URL(blogurl);


            System.out.println(url);

            Log.d("doInBackground", "###########try to open URL: " + url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            // Starts the query
            urlConnection.connect();

            Log.d("doInBackground", "###########try to open Stream:");

            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            //Parser supports XML namespaces
            factory.setNamespaceAware(true);
            //Provides the methods needed to parse XML documents
            XmlPullParser parser = factory.newPullParser();
            //InputStreamReader converts bytes of data into a stream
            parser.setInput(stream, null);

            //parser wird funktion übergeben

            finDownloading = getRSS(parser);

            urlConnection.disconnect();
            stream.close();

            //save xml for later
            download_feed(blogname,blogurl);


        } catch (Exception e) {
            //falls was schiefgeth soll die gesavete xml datei geladen werden
            e.printStackTrace();

            Log.d("Exception", "###########unable zu open Connection");

            try {

                File file = new File(context.getFilesDir(), blogname + "_save.xml");
                FileInputStream fileInputStream = new FileInputStream(file);
                XmlPullParserFactory xml = XmlPullParserFactory.newInstance();
                xml.setNamespaceAware(true);
                XmlPullParser parser = xml.newPullParser();
                parser.setInput(fileInputStream, null);
                toast = true;
                finDownloading = getRSS(parser);


            } catch (XmlPullParserException e1) {
                e1.printStackTrace();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
                Log.d("Exception", "###########unable zu open Connection or save.xml");

            }


        }

        //////////////////////#######################################falls die news zu groß sind, das Androis mehr als 64k
        //////////////////////#######################################kommt es zu abstürzen, daher maximal 20 einträge
        //////////////////////#######################################       das müss ich ändern
        int a_size = news.size();
        if(a_size >20){
            for(int i = a_size; i > 20; i--){
                news.remove(i-1);
            }
        }


        bundle.putString("blogName", params[1]);
        bundle.putParcelableArrayList("rssFeed", news);
        Log.d("doInBackground", "###########return");

        return bundle;
    }

    @Override
    protected void onPostExecute(Bundle news) {

        Log.d("onPostExecute", "##########got called");


        if (toast == true) {
            Toast.makeText(context, "read save_xml", Toast.LENGTH_SHORT).show();
        }

        //warum != null ??
        if (myInterface != null) {

            System.out.print(news.size());
            myInterface.postResult(news);
            Log.d("onPostExecute", "##########Interface Return loop got called");

        } else {
            Log.e("onPostExecute", "##########Interface DID NOT RETURN!");
        }
        Log.d("onPostExecute", "##########onPostExecute finished");

    }

    public boolean getRSS(XmlPullParser parser) throws XmlPullParserException {


        try {

            Log.d("getRSS", " ##################getRSS wird aufgerufen");

            //Get the currently targeted event type, beispiel title oder item
            int eventType = parser.getEventType();
            String eventName;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                //nextline
                parser.next();

                //bekommt eventType und extrahiert den Namen: "item" "title"
                eventType = parser.getEventType();
                eventName = parser.getName();

                if ((eventName instanceof String && eventName.equals("item")) || (eventName instanceof String && eventName.equals("entry"))) {

                    parser.next();
                    news.add(prosessingNews(parser));

                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            Log.d("getRSS", "OH NO; SOMETHING BAD HAPPEND!!!!!!!!!! :( " + " - Your xml file maight have a mistake in its format?! try later again");
            Toast.makeText(context, "OH NO; SOMETHING BAD HAPPEND!!!!!!!!!! :( ", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        Log.d("getRSS", " ##################getRSS erfolgreich");


            /*
            for (int i=0;i<news.size();i++) {
                Log.d("array", "##############" + news.get(i)[0]);
            }
            Log.d("a",Integer.toString(a) + "-" + Integer.toString(news.size()));
            */
        return true;

    }

    private Rss_Data_Struckture prosessingNews(XmlPullParser parser) throws URISyntaxException, IOException, XmlPullParserException, NullPointerException {

        String rssHeadline = "";
        String rsslink = "";
        String rssdescription = "";
        String rsspubDate = "";
        String rssimage = "";

        Log.d("prosessingNews()", "########got called");
        a++;

        int eventType = parser.getEventType();
        String eventName = parser.getName();


        while (eventType != XmlPullParser.END_DOCUMENT && !((eventName instanceof String && eventName.equals("item")) || (eventName instanceof String && eventName.equals("entry")))) {

            //gets title......
            if (eventType == XmlPullParser.START_TAG && (eventName != null && (eventName.equals("title")) && rssHeadline == "")) {

                parser.next();
                rssHeadline = parser.getText();

            }

            if (eventType == XmlPullParser.START_TAG && (eventName != null && (eventName.equals("link")))) {

                parser.next();
                rsslink = parser.getText();

            }

            if (eventType == XmlPullParser.START_TAG && (eventName != null && (eventName.equals("description") || eventName.equals("content")) && rssdescription == "")) {

                parser.next();

                String[] line = new String[2];

                line = descrANDimage(parser.getText());

                rssimage = line[0];
                rssdescription = line[1];
                //rssdescription = (String) bundle.get("desc");

            }

            if (eventType == XmlPullParser.START_TAG && (eventName != null && (eventName.equals("pubDate")))) {

                parser.next();
                rsspubDate = parser.getText();

            }

            parser.next();

            eventType = parser.getEventType();
            eventName = parser.getName();

        }

        if (rssHeadline.length() != 0 || rssdescription.length() != 0 || rsslink.length() != 0) {

            Rss_Data_Struckture rssData = new Rss_Data_Struckture(rssHeadline, rsslink);
            rssData.setRssdescription(rssdescription + "\n\n" + rssimage);
            rssData.setImage(rssimage);
            rssData.setRssPubDate(rsspubDate);


            //Log.d("prosessingNews", rssHeadline + " - " + rsslink + " - " + rssdescription + " - " + rsspubDate);
            //Log.d("prosessingNews", rssData.getRssTitle() + " - " + rssData.getRssLink() + " - " + rssData.getRssDescription() + " - " + rssData.getRssPubDate());

            return rssData;

        }

        return null;
    }

    private String[] descrANDimage(String line) {

        //to store image and description


        line = line.replaceAll("&lt;", "<");
        line = line.replaceAll("&gt;", ">");
        /*
        line = line.replaceAll("&#[0-9]+;","");
        */

        String image = "";
        String desc = line;
        String[] liste = new String[2];
        //regx

        //Log.d("imageANDdesc",line);


        String imageRegx = "(src=\")(.*(?i)(jpg|png|bmp|jpeg))";

        Pattern imagePattern = Pattern.compile(imageRegx);

        Matcher imageMatch = imagePattern.matcher(line);


        Log.d("imageANDdesc", "so far so good");

        if (imageMatch.find()) {
            image = imageMatch.group(2);
            Log.d("imageANDdesc", "image");
            System.out.println(image);
        } else {
            //System.out.println(line);
        }


        //entfernt zeilenumbüche und html-gedönse
        //desc = desc.replaceAll("(<br/><br/>)", "");
        desc = desc.split("<br clear='all'/>")[0];

        int pos = desc.indexOf("<br clear='all'/>");
        if (pos != -1) {
            desc = desc.substring(0, pos);
        }

        desc = desc.replaceAll("(<img.*?(>))", "");

        //desc = desc.trim();

        liste[0] = image;
        liste[1] = desc;

        return liste;
    }


    private void download_feed(String name,String rss_url){

        Log.d("Async_downoad_for_later", "gets called");

        URL url;
        FileOutputStream outputStream = null;
        HttpURLConnection httpURLConnection;
        InputStream inputStream = null;


            try {
                Log.d("Async_downoad_for_later", "loading url: " + name);

                url = new URL(rss_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(10000 /* milliseconds */);
                httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                outputStream = context.openFileOutput(name + "_save.xml",context.MODE_PRIVATE);

                byte data[] = new byte[8096];
                int count;
                while ((count = inputStream.read(data)) != -1){
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
}
