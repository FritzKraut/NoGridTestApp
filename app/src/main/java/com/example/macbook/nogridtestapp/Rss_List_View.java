package com.example.macbook.nogridtestapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;



import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



/*
*
*
*
* erzeugt ListView der News
*
*
* 
*
* */


public class Rss_List_View extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Rss_Data_Struckture> rss_news_list = new ArrayList<Rss_Data_Struckture>();
    private LruCache<String, Bitmap> mMemoryCache;
    int cacheSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss__list__view);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //returnbutten in der actionbar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Log.d("Rss_List_View", "Rss_List_view was called");


        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        addBitmapToMemoryCache("loading_icon", BitmapFactory.decodeResource(getResources(), R.drawable.loading_icon_big_png));


        //gets Intent object from calling intent
        Bundle bundle = getIntent().getExtras();

        rss_news_list = bundle.getParcelableArrayList("rss_news_list");
        getSupportActionBar().setTitle(bundle.getCharSequence(("blogName")));


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        News_Item_Adapter mAdapter = new News_Item_Adapter(this, rss_news_list);
        mRecyclerView.setAdapter(mAdapter);


        /*
        ListAdapter news_adapter = new News_Item_Adapter(this, rss_news_list);
        listView.setAdapter(news_adapter);

        listView.setOnItemClickListener(this);
*/




        //Log.d("Rss_List_View", "got parceled");
        //Log.d("Rss_List_View",String.valueOf(rss_news_list.isEmpty()) + " - " +String.valueOf(bundle.isEmpty()));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rss__list__view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d("onItemClick()", "OnItemClick was called");

        String url = rss_news_list.get(position).getRssLink();

        Uri webpage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW,webpage);

        System.out.println(url);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }


    }


    public class News_Item_Adapter extends RecyclerView.Adapter<Rss_List_View.News_Item_Adapter.ViewHolder> {

        //inflate the item xml
        private LayoutInflater inflater;
        private ArrayList<Rss_Data_Struckture> rss_feed;

        public News_Item_Adapter(Context context,ArrayList<Rss_Data_Struckture> rss_feed){

            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.rss_feed = rss_feed;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return rss_feed.size();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public Rss_List_View.News_Item_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {


            Log.d("News_Item_Adapter", "###############get View wurde aufgerufen");
            Log.d("News_Item_Adapter", Integer.toString(rss_feed.size()));

            View view;
            view = inflater.inflate(R.layout.rss_card,viewGroup,false);

            Rss_List_View.News_Item_Adapter.ViewHolder holder = new Rss_List_View.News_Item_Adapter.ViewHolder(view);


            return holder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final Rss_List_View.News_Item_Adapter.ViewHolder viewHolder, int i) {


            final Rss_Data_Struckture rss_data = rss_feed.get(i);

            viewHolder.title.setText(Html.fromHtml(rss_data.getRssTitle()));
            viewHolder.description.setText(Html.fromHtml(rss_data.getRssDescription()));
            viewHolder.pubdate.setText(Html.fromHtml(rss_data.getRssPubDate()));

            viewHolder.web.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri webpage = Uri.parse(rss_data.getRssLink());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (webIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(webIntent);
                    }
                }
            });

            viewHolder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String webpage = rss_data.getRssLink();
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");   //wichtig für die art des Intents

                    sendIntent.putExtra(Intent.EXTRA_TEXT, webpage);
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_Dialog)));
                }
            });

            //image key
            //image
            final String imageKey = String.valueOf(rss_feed.get(i).getImage());
            final Bitmap bitmap = getBitmapFromMemCache(imageKey);
            //wenn es ein image im cache gibt wird dieses benutzt
            if (bitmap != null) {
                viewHolder.imageView.setImageBitmap(bitmap);
            }
            else {
                if (cancelPotentialWork(rss_data.getImage(), viewHolder.imageView)) {
                    //wichtig, sonst werden beim scrollen die falschen bilder gezeigt
                    viewHolder.imageView.setImageBitmap(getBitmapFromMemCache("loading_icon"));
                    Async_Article_imageView my_imageTask = new Async_Article_imageView(viewHolder.imageView);
                    my_imageTask.execute(rss_data.getImage());
                }
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView imageView = null;
            public TextView title;
            public TextView description;
            public TextView pubdate;
            public Button web,share;

            public ViewHolder(View itemView) {
                super(itemView);

                imageView = (ImageView) itemView.findViewById(R.id.rss_image_view);
                title = (TextView) itemView.findViewById(R.id.rss_title_view);
                description = (TextView) itemView.findViewById(R.id.rss_description_view);
                pubdate = (TextView) itemView.findViewById(R.id.rss_date_view);
                web = (Button) itemView.findViewById(R.id.buttonGoOnline);
                share = (Button) itemView.findViewById(R.id.buttonShare);
            }

        }

    }


    /**
     * wenn ein Async_task gerade ausgeführt wird der gerade nicht benötigt wird, wird dieser gecancelt
     * */
    public static boolean cancelPotentialWork(String resId, ImageView imageView) {
        final Async_Article_imageView async_imageView = getAsync_imageView(imageView);

        if (async_imageView != null) {
            final String async_TaskResId = async_imageView.name;
            if (async_TaskResId != resId) {
                // Cancel previous task
                async_imageView.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }


    /**
    * die funktion guckt ob das image im imageView, guckt ob es eine instance von AsyncDrawable ist.
    * wenn ja, wird das bild zu einem AsyncDrawable gecastet, weil es eine subclass von BitmapDrawable ist
    * (ich hab die reference super() nicht richtig implementiert)!
    * die funktion return das Async_task bzw. das vertige imageView
    *
    * hab leider nicht ganz verstanden was das ganze soll....
    * */
    private static Async_Article_imageView getAsync_imageView(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getAsync_imageView();
            }
        }
        return null;
    }


    /**
     * Created by macbook on 9/4/15.
     *
     *
     *
     *
     * Erzeugt ImageView für den ArtikelView
     *
     *
     * Nächstes mal, Async_Article_imageView und Async_ImageView zusammenfassen
     * Weniger Code Ist Besserer Code
     *
     */
    public class Async_Article_imageView extends AsyncTask<String,Void,Bitmap> {


        private final WeakReference<ImageView> imageViewReference;
        private Bitmap bitmap = null;
        private String name;


        public Async_Article_imageView(ImageView imageView){
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.d("Async_Article_imageView", String.valueOf(params[0] == null));

            if(params[0] != null) {
                load_ImageFromUrl(params[0]);
            }
            else {
                load_Placeholder();
            }

            return bitmap;
        }


        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (isCancelled()) {
                bitmap = null;
            }
            //set Bitmap to imageview

            if (imageViewReference != null && bitmap != null) {

                final ImageView imageView = imageViewReference.get();
                final Async_Article_imageView async_imageView = getAsync_imageView(imageView);
                imageView.setImageBitmap(bitmap);
                }


            return;

        }

        private void load_ImageFromUrl(String image_url){

            Log.d("Async_Article_imageView", "load_ImageFromUrl");

            name = image_url;


            try {
                URL url = new URL(image_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                HttpURLConnection httpURLConnectionReset = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                InputStream inputStreamReset = new BufferedInputStream(httpURLConnectionReset.getInputStream());
                //inputStream.mark(0);

                //wir öffnen den stream, decoden stream und ohne speicher freizugeben, berechnen benötigte größe
                // anhand der größe des imageviewers
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream,null,options);

                //Calculate inSampleSize
                //options.inSampleSize = calculateInSampleSize(options, imageViewReference.get().getWidth(), imageViewReference.get().getHeight());
                options.inSampleSize = calculateInSampleSize(options, 600, 400);

                //inputStream muss geresetet werden, da er vorher einmal durchgelaufen ist, sonst retrunt der stream null
                //Funtioniert nicht, lieber einen zweiten stream öffnen, siehe oben
                //inputStream.reset();

                options.inJustDecodeBounds = false;
                // Decode bitmap with inSampleSize set
                bitmap = BitmapFactory.decodeStream(inputStreamReset,null,options);
                addBitmapToMemoryCache(image_url, bitmap);

                inputStream.close();
                httpURLConnection.disconnect();
                inputStreamReset.close();
                httpURLConnectionReset.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("Async_Article_imageView", "Could Not Load Image");
                load_Placeholder();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Async_Article_imageView","Could Not Load Image");
                load_Placeholder();
            }
            finally {

            }


            return;

        }

        private void load_Placeholder(){

            Log.d("load_Placeholder()","load_Placeholder");

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap placeholder = getBitmapFromMemCache("Placeholder");

            if(!(placeholder == null)){
                Log.d("load_Placeholder()","Reload Placeholder");
                bitmap = placeholder;
            }
            else {
                try {
                    BitmapFactory.decodeResource(getResources(), R.drawable.placeholder_black2, options);
                    options.inSampleSize = calculateInSampleSize(options, 600, 400);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder_black2, options);
                    addBitmapToMemoryCache("Placeholder", bitmap);
                    Log.d("load_Placeholder()","Placeholder has been resized loaded");

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
            return;
        }


    }


    /**
     * berechnet die richtige größe des bildes
     * */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (reqWidth == 0 || reqHeight == 0) return inSampleSize;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        //Log.d("calculateInSampleSize",String.valueOf(inSampleSize));
        return inSampleSize;
    }



    /**
     * ich weiß leider nich genau was das soll...
     *
     *
     * */
    class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<Async_Article_imageView> bitmapWorkerTaskReference;

        public AsyncDrawable(ImageView imageView,
                             Async_Article_imageView bitmapWorkerTask) {
            super();
            bitmapWorkerTaskReference =
                    new WeakReference<Async_Article_imageView>(bitmapWorkerTask);
        }

        public Async_Article_imageView getAsync_imageView() {
            return bitmapWorkerTaskReference.get();
        }
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            Log.d("addBitmapToMemoryCache()","füge bitmap in cache ein");
            mMemoryCache.put(key, bitmap);
            //die größe des geresampleten bitmap würde ich gern auf 600x400 beschrenken!
            //Log.d("addBitmapToMemoryCache()",String.valueOf(mMemoryCache.get("Placeholder")!=null)+" CacheSize:"+mMemoryCache.size()+" - ImageSize: "+String.valueOf(bitmap.getByteCount()/1024) +" bitmapDimensions: "+bitmap.getWidth() +"x"+bitmap.getHeight());
        }
    }


    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


}
