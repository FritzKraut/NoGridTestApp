package com.example.macbook.nogridtestapp.my_adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.macbook.nogridtestapp.Blog_Data_Struckture;
import com.example.macbook.nogridtestapp.MainActivity;
import com.example.macbook.nogridtestapp.R;
import com.example.macbook.nogridtestapp.Rss_Data_Struckture;
import com.example.macbook.nogridtestapp.async_classes.Async_ImageView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



/**
 * Created by macbook on 6/23/15.
 *
 *
 */
public class MyAdapter extends BaseAdapter {


    private LayoutInflater inflater;
    private ArrayList<Blog_Data_Struckture> bloglist;
    private Typeface typo = null;
    private Context context;
    private LruCache<String,Bitmap> mMemoryCache;
    private Bitmap bitmap_loader = null;



    /**
     *
     * ???????????????????????????
     *
     * @param context ????????????
     * @param bloglist ???????????
     * @param mMemoryCache ???????
     */
    public MyAdapter(Context context, ArrayList<Blog_Data_Struckture> bloglist,LruCache<String,Bitmap> mMemoryCache){

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bloglist = bloglist;
        this.context = context;
        this.mMemoryCache = mMemoryCache;
        this.bitmap_loader = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading_icon_big_jpg);

                Log.d("MyAdapter", "###############construktor wurde aufgerufen");

    }



    //WICHTIG FÜR das erstellen der views
    @Override
    public int getCount() {

        return bloglist.size();
    }

    @Override
    public Object getItem(int position) {
        return bloglist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    /**
     *
     *  ????????????????????????????????????????????
     *
     * @param position ????????????????
     * @param convertView ?????????????
     * @param parent ??????????????????
     * @return ????????????????????????
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("MyAdapter","###############get View wurde aufgerufen");

        View view;
        ViewHolder holder;

        if(convertView == null){

            view = inflater.inflate(R.layout.list_item,parent,false);
            holder = new ViewHolder();

            holder.imageView = (ImageView) view.findViewById(R.id.image_view);
            holder.title = (TextView) view.findViewById(R.id.text_view_title);
            holder.description = (TextView) view.findViewById(R.id.blog_description);
            view.setTag(holder);
        }
        else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        Blog_Data_Struckture rss_data = bloglist.get(position);
        holder.title.setText(rss_data.getBlogName());
        holder.description.setText(rss_data.getBlogDescription());

        holder.imageView.setImageBitmap(bitmap_loader);
        //übergebe die imageview referenz und lade asyncron die bilder
        Async_ImageView async_imageView = new Async_ImageView(context,holder.imageView);
        async_imageView.execute(rss_data.getBlogName());

        return view;
    }

    /*
    * Diese Klasse beinhaltet alle View die für das layout der Blogeinträge
    * nötig sind
    */
    public static class ViewHolder {

        public ImageView imageView;
        public TextView title;
        public TextView description;
    }



















}
