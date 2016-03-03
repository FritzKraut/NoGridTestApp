package com.example.macbook.nogridtestapp;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import com.example.macbook.nogridtestapp.my_adapter.BlogAdapter;
import com.example.macbook.nogridtestapp.my_fragments.Add_Dialog_Fragment;
import com.example.macbook.nogridtestapp.my_interface.Add_Dialog_Interface;
import com.example.macbook.nogridtestapp.my_interface.AsyncResponse;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.macbook.nogridtestapp.async_classes.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


/*
* appcompatactivity because of teh actionbar und und angewendete theme!!!!
* ActionBarActivity is OUT DATED!!!
*
*
*
*
*
*
*/


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AsyncResponse, Add_Dialog_Interface, BlogAdapter.ClickListner{

    private ArrayList<Blog_Data_Struckture> blogList = new ArrayList<Blog_Data_Struckture>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    //BitmapCache
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "##############Activity started");
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        //load all the stored blogs

        //reset();
        boolean load = loadStorage();

        Log.d("MainActivity", "###############alles initialisiert");
        Log.d("storage", String.valueOf(blogList.isEmpty()));

        setUpCache();

        //sets ui
        uiFunction();

        Log.d("MainActivity", "Ende Der Geschichte");

    }
    private void setUpCache() {

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }

        };


    }

    private void uiFunction() {

        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        BlogAdapter mAdapter = new BlogAdapter(this, blogList);
        mAdapter.setclickListener(this);
        mRecyclerView.setAdapter(mAdapter);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            add_dialog();
            return true;
        }

        if (id == R.id.action_remove) {
            return true;
        }

        if (id == R.id.action_autorenew) {
            ArrayList<String> array = new ArrayList<String>();
            for (int i = 0; i < blogList.size(); i++) {
                array.add(blogList.get(i).getBlogName());
                array.add(blogList.get(i).getRssFeedURL());
            }

            //mProgress.setVisibility(View.VISIBLE);

            item.setActionView(R.layout.action_view);
            item.expandActionView();

            Async_downoad_for_later myTask = new Async_downoad_for_later(this, item);
            myTask.execute(array);

        }

        return super.onOptionsItemSelected(item);
    }

    private void reset() {

        String storage = "blog.txt";

        File file = new File(this.getFilesDir(), storage);
        file.delete();
        initialDataArray();
    }

    private boolean initialDataArray() {

        Log.d("initialDataArray()", "########got called");

        String[] blogData1 = {"Fefes Blog", "http://blog.fefe.de/rss.xml", "Verschwörungen und Obskures aus aller Welt", "http://blog.flattr.net/wp-content/uploads/2011/10/Screen-Shot-2011-10-31-at-21.45.43-231x300.png"};
        String[] blogData2 = {"Golem", "http://golem.de.dynamic.feedsportal.com/pf/578068/http://rss.golem.de/rss.php?feed=RSS2.0", "IT-News fuer Profis", "http://www.golem.de/1105/83307-11066-i.png"};
        String[] blogData3 = {"TechCrunch", "https://feeds.feedburner.com/TechCrunch/", "TechCrunch is a group-edited blog that profiles the companies, products and events defining and transforming the new web.", "https://tctechcrunch2011.files.wordpress.com/2014/04/tc-logo.jpg"};
        String[] blogData4 = {"The Verge - All Posts", "http://www.theverge.com/rss/index.xml", "-", "http://media5.starkinsider.com/wordpress/wp-content/uploads/2008/12/the-verge-logo-300x200.png"};
        String[] blogData5 = {"Chip", "http://rss.chip.de/c/573/f/7440/index.rss", "Aktuelle Top-News bei Chip.de", "http://www.chip.de/i/chip_online_logo.gif"};
        String[] blogData6 = {"NetzpolitiK.org", "https://netzpolitik.org/feed", "Politik in der digitalen Gesellschaft.", "http://i.computer-bild.de/imgs/6/7/2/4/1/9/3/Logo-von-netzpolitik-org-658x370-fa69f945f3d137d2.jpg"};
        String[] blogData7 = {"Gizmodo", "http://feeds.gawker.com/gizmodo/full", "Everything Is Technology", "http://quotebookapp.com/img/gizmodo-logo.png"};
        String[] blogData8 = {"Kotaku", "http://feeds.gawker.com/kotaku/full", "The Gamer's Guide", "http://www.playsidestudios.com/wp-content/uploads/2011/12/kotaku_logo.png"};
        String[] blogData9 = {"Wired", "http://feeds.wired.com/wired/index", "Top Stories", "https://miraclefeet.org/wp-content/uploads/2014/09/wired.gif"};
        String[] blogData10 = {"Digg", "https://digg.com/rss/top.rss", "Digg - What the Internet is talking about right now", "https://static1.squarespace.com/static/544c2fefe4b0dfe393bd098c/544cc1f0e4b0420e88430f4c/544cc5b5e4b0714dbcca1c92/1414317493528/Digg.png"};
        String[] blogData11 = {"Lifehacker", "http://feeds.gawker.com/lifehacker/full", "Tips and downloads for getting things done", "http://grasshopper.com/blog/wp-content/uploads/2013/10/lifehacker-300x98.jpg"};
        String[] blogData12 = {"VICE RSS Feed", "https://www.vice.com/rss", "RSS feed for VICE.com", "http://www.vice.com/assets/images/vice/og/og-image.jpg"};
        String[] blogData13 = {"VICE.de RSS Feed", "https://www.vice.com/de/rss", "RSS feed for VICE.com/de", "http://www.vice.com/assets/images/vice/og/og-image.jpg"};
        String[] blogData14 = {"R-bloggers", "https://feeds.feedburner.com/RBloggers?format=xml", "R news and tutorials contributed by (573) R bloggers", "-"};
        String[] blogData15 = {"BBC News", "http://feeds.bbci.co.uk/news/world/rss.xml?edition=uk", "The latest stories from the World section of the BBC News web site.", "http://news.bbcimg.co.uk/nol/shared/img/bbc_news_120x60.gif"};
        String[] blogData16 = {"Der Postillon", "https://feeds.feedburner.com/blogspot/rkEL?format=xml", "Ehrliche Nachrichten - unabhängig, schnell, seit 1845", "http://4.bp.blogspot.com/-46xU6sntzl4/UVHLh1NGfwI/AAAAAAAAUlY/RiARs4-toWk/s400/Logo.jpg"};
        String[] blogData17 = {"The Week", "http://www.theweek.co.uk/feeds/all", "-", "http://imgs.keralamatrimony.com/bmimgs/bm-awards-week-big-logo.gif"};
        String[] blogData18 = {"SPIEGEL ONLINE - Schlagzeilen", "http://www.spiegel.de/schlagzeilen/tops/index.rss", "Deutschlands führende Nachrichtenseite. Alles Wichtige aus Politik, Wirtschaft, Sport, Kultur, Wissenschaft, Technik und mehr.", "http://www.spiegel.de/static/sys/logo_120x61.gif"};
        String[] blogData19 = {"FOXNews.com", "http://feeds.foxnews.com/foxnews/latest?format=xml", "FOX News Channel - We Report. You Decide.", "http://www.foxnews.com/images/headers/fnc_logo.gif"};
        String[] blogData20 = {"The Intercept", "https://theintercept.com/feed/?rss", "-", "https://pbs.twimg.com/profile_images/621138310585409536/mSJHNbO6_400x400.png"};
        String[] blogData21 = {"Network Front | The Guardian", "http://www.theguardian.com/uk/rss", "Latest news, sport, business, comment, analysis and reviews from the Guardian, the world's leading liberal voice", "https://static.guim.co.uk/sys-images/Guardian/Pix/pictures/2015/3/17/1426556324831/glogo-460x276.jpg"};
        String[] blogData22 = {"TMZ.com", "https://www.tmz.com/rss.xml", "Celebrity Gossip and Entertainment News, Covering Celebrity News and Hollywood Rumors. Get All The Latest Gossip at TMZ - Thirty Mile Zone.", "http://www.brandsoftheworld.com/sites/default/files/styles/logo-thumbnail/public/032012/tmz.ai_.png?itok=bKhptU-O"};
        String[] blogData23 = {"The Local", "http://fulltextrssfeed.com/www.thelocal.se/RSS/theLocal.xml", "Sweden's news in English", "https://pbs.twimg.com/profile_images/544440302778339331/7M1xEuKY.png"};
        String[] blogData24 = {"Metronaut.de | Metronaut.de", "http://www.metronaut.de/feed/", "Big Berlin Bullshit", "https://www.metronaut.de/wp-content/cache/podlove/b9/36463846f87a361ca8cfa925f6c66c/metrolaut_400x400.png"};
        String[] blogData25 = {"Radio Schweden", "http://api.sr.se/api/rss/program/2108", "Aktuelles aus Schweden", "https://s-media-cache-ak0.pinimg.com/736x/d5/e4/d3/d5e4d3f6bc45d352b87392535f59da91.jpg"};
        String[] blogData26 = {"Aktuell - FAZ.NET", "http://www.faz.net/rss/aktuell/", "News, Nachrichten und aktuelle Meldungen aus allen Ressorts. Politik, Wirtschaft, Sport, Feuilleton und Finanzen im Überblick.", "http://www.gmkfreelogos.com/logos/F/img/FAZ_NET_Frankfurter_Allgemeine_Zeitung.gif"};
        String[] blogData27 = {"Vox - All", "https://www.vox.com/rss/index.xml", "-", "https://cdn1.vox-cdn.com/assets/4395727/dribbble_vox_large.jpg"};

        String[][] blogDataArray = {blogData1, blogData2, blogData3, blogData4, blogData5, blogData6, blogData7, blogData8, blogData9, blogData10,
                blogData11, blogData12, blogData13, blogData14, blogData15, blogData16, blogData17, blogData18, blogData19, blogData20,
                blogData21,blogData22,blogData23,blogData24,blogData25,blogData26,blogData27};

        for (int i = 0; i < blogDataArray.length; i++) {
            addToStorage(blogDataArray[i][0], blogDataArray[i][1], blogDataArray[i][2], blogDataArray[i][3]);
            Async_Thumbnails thumbnails = new Async_Thumbnails(this);
            thumbnails.execute(blogDataArray[i][0], blogDataArray[i][3]);

        }


        return true;
    }


    private void initialRssData(String blog, String url) {

/*        if(blogMap.get(blog) == null){
        }

  */

        Blog_Data_Struckture rssBlog = new Blog_Data_Struckture(blog, url, "", "");

        blogList.add(rssBlog);
        Log.d("initialRssData", "###############construktor wurde aufgerufen");
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d("onItemClick()", "OnItemClick was called");

        get_and_view_RssNews_Object(position);

    }


    public void get_and_view_RssNews_Object(int position) {

        Log.d("get_and_view_RssNews()", "----------------" + blogList.get(position).getBlogName());

        //get url
        String url = blogList.get(position).getRssFeedURL();

        try {

            if (blogList.get(position).getRssNewsList() != null) {

                Intent intent = new Intent(this, Rss_List_View.class);
                intent.putParcelableArrayListExtra("rss_news_list", blogList.get(position).getRssNewsList());
                intent.putExtra("blogName", blogList.get(position).getBlogName());
                startActivity(intent);
            } else {

                String blogName = blogList.get(position).getBlogName();
                AsyncTaskURL myTask = new AsyncTaskURL(this);
                myTask.myInterface = this;
                myTask.execute(url, blogName);

                Log.d("get_and_view_RssNews_Object", "Finished");

            }

        } catch (Exception e) {
            Log.d("EXCEPTIOn", "wrong XML format?! no internetConnection?!");
            Toast.makeText(this, "could not open url:\n" + url, Toast.LENGTH_SHORT).show();

            e.printStackTrace();
            //falls etwas schief geht wir der bog auf false gesetzt
            blogList.get(position).setEveryThingAlright(false);
        }
    }

    //läg die blogs aus dem internal storage
    private boolean loadStorage() {

        Log.d("loadSorage()", "########got called");

        String storage = "blog.txt";

        //try to open storage file
        File file = new File(this.getFilesDir(), storage);

        if (file.exists()) {
            //if the storage file exist, it
            try {


                try {

                    FileInputStream fileInputStream = new FileInputStream(file);
                    XmlPullParserFactory xml = XmlPullParserFactory.newInstance();
                    xml.setNamespaceAware(true);
                    XmlPullParser parser = xml.newPullParser();
                    parser.setInput(fileInputStream, null);
                    parser.next();

                    //Get the currently targeted event type, beispiel title oder item
                    int eventType = parser.getEventType();
                    String eventName;
                    String title = "";
                    String source = "";
                    String subtitle = "";
                    String pic = "";

                    while (eventType != XmlPullParser.END_DOCUMENT) {

                        //bekommt eventType und extrahiert den Namen: "item" "title"
                        eventType = parser.getEventType();
                        eventName = parser.getName();

                        if (eventName instanceof String && eventName.equals("Blog")) {
                            System.out.println("IF - LOADING THE BLOG JO");


                            parser.next();
                            eventType = parser.getEventType();
                            eventName = parser.getName();

                            while (eventType != XmlPullParser.END_DOCUMENT && !(eventName instanceof String && eventName.equals("Blog"))) {
                                //System.out.println("while - LOADING THE BLOG JO");

                                //gets title......
                                if (eventType == XmlPullParser.START_TAG && (eventName != null && (eventName.equals("Title")))) {

                                    parser.next();
                                    title = parser.getText();
                                    System.out.println(title);
                                    parser.next();


                                }
                                if (eventType == XmlPullParser.START_TAG && (eventName != null && (eventName.equals("Source")))) {

                                    parser.next();
                                    source = parser.getText();
                                    System.out.println(source);
                                    parser.next();

                                }
                                if (eventType == XmlPullParser.START_TAG && (eventName != null && (eventName.equals("Subtitle")))) {

                                    parser.next();
                                    subtitle = parser.getText();
                                    System.out.println(subtitle);
                                    parser.next();


                                }
                                if (eventType == XmlPullParser.START_TAG && (eventName != null && (eventName.equals("Picture")))) {

                                    parser.next();
                                    pic = parser.getText();

                                    System.out.println(pic+" ");

                                }
                                parser.next();
                                eventType = parser.getEventType();
                                eventName = parser.getName();

                            }


                            System.out.println(title + source + subtitle + pic);
                            Blog_Data_Struckture rssBlog = new Blog_Data_Struckture(title, source, subtitle, pic);
                            blogList.add(rssBlog);
                            parser.next();

                        }

                    }


                } catch (XmlPullParserException e) {
                    Toast.makeText(this, "Loading Blog Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                return false;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } else {
            //if not use old initial function to creat it
            Log.d("loadStorage", "#######file does not exist");

            initialDataArray();
            return true;

        }
    }

    //wenn der plus button gedrückt wird
    private void add_dialog() {

        FragmentManager manager = getFragmentManager();
        Add_Dialog_Fragment add_dialog = new Add_Dialog_Fragment();
        add_dialog.show(manager, "add_dialog");

    }

    private boolean addToStorage(String blog, String url, String description, String image) {


        Log.d("addToStorage()", "###########got called");
        String storage = "blog.txt";

        FileOutputStream outputStream = null;
        try {
            //open storage file and append new blog and url
            outputStream = openFileOutput(storage, Context.MODE_APPEND);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write("<Blog>\n<Title>" + blog + "</Title>\n<Source>" + url + "</Source>\n<Subtitle>" +
                    description + "</Subtitle>\n<Picture>" + image + "</Picture>\n</Blog>\n");
            outputStreamWriter.close();
            outputStream.close();

            Toast.makeText(this, "update - " + blog + "\n" + url, Toast.LENGTH_SHORT).show();
            //update UI
            uiFunction();

            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }
    }

    public void onDestroy() {

        super.onDestroy();

        finish();
    }

    @Override
    public void postResult(Bundle bundle) {
        /*
        * gets called when asynctask finished and get the news result from it
        *
        * starts intent
        * */

        Log.d("postResult", "######got called");
        Intent intent = new Intent(this, Rss_List_View.class);
        intent.putParcelableArrayListExtra("rss_news_list", bundle.getParcelableArrayList("rssFeed"));
        intent.putExtra("blogName", bundle.getString("blogName"));
        startActivity(intent);

    }

    @Override
    public void onFinishAddDialog(String name, String url, String description, String image) {
       /*
        * gets called when add_dilog finished and gets tested blog url result from it
        *
        * */

        Toast.makeText(this, name + url + description, Toast.LENGTH_SHORT);
        //System.out.println(name + " - " + url + " - " + description);
        addToStorage(name, url, description, image);

    }


    @Override
    public void ItemClicked(View view, int position) {
        get_and_view_RssNews_Object(position);

    }
}

