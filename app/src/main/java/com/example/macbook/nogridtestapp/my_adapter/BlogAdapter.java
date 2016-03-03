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
public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {


    private LayoutInflater inflater;
    private ArrayList<Blog_Data_Struckture> bloglist;
    private Typeface typo = null;
    private Context context;
    private Bitmap bitmap_loader = null;
    private ClickListner clickListener;


    /**
     *
     * ???????????????????????????
     *
     * @param context ????????????
     * @param bloglist ???????????
     */
    public BlogAdapter(Context context, ArrayList<Blog_Data_Struckture> bloglist){
        super();

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bloglist = bloglist;
        this.context = context;
        this.bitmap_loader = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading_icon_big_jpg);

        Log.d("MyAdapter", "###############construktor wurde aufgerufen");

    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return bloglist.size();
    }


    /**
     *
     *  ????????????????????????????????????????????
     *
     * @param position ????????????????
     * @return ????????????????????????
     */
    @Override
    public BlogAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        Log.d("MyAdapter","###############get View wurde aufgerufen");

        View view;
        view = inflater.inflate(R.layout.list_item,viewGroup,false);

        BlogAdapter.ViewHolder holder = new BlogAdapter.ViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(BlogAdapter.ViewHolder holder, int position) {
        final Blog_Data_Struckture blog_item = bloglist.get(position);


        Log.d("get name","###############"+String.valueOf(blog_item.getBlogName()));


        holder.title.setText(blog_item.getBlogName());
        holder.description.setText(blog_item.getBlogDescription());

        holder.imageView.setImageBitmap(bitmap_loader);
        //übergebe die imageview referenz und lade asyncron die bilder
        Async_ImageView async_imageView = new Async_ImageView(context,holder.imageView);
        async_imageView.execute(blog_item.getBlogName());
    }

    public void setclickListener(ClickListner clicklistener){
        this.clickListener = clicklistener;
    }
    /*
    * Diese Klasse beinhaltet alle View die für das layout der Blogeinträge
    * nötig sind
    */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageView;
        public TextView title;
        public TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            title = (TextView) itemView.findViewById(R.id.text_view_title);
            description = (TextView) itemView.findViewById(R.id.blog_description);
        }


        @Override
        public void onClick(View view) {

            if (clickListener != null) {
                clickListener.ItemClicked(view,getAdapterPosition());
            }
        }
    }


    public interface ClickListner{

        public void ItemClicked(View view, int position);

    }
}
