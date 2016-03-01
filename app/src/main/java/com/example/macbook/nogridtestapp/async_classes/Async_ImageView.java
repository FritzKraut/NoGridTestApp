package com.example.macbook.nogridtestapp.async_classes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.example.macbook.nogridtestapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by macbook on 9/3/15.
 *
 *
 *
 *
 * Erzeugt ImageView für den BlogView
 *
 *
 *
 */
public class Async_ImageView extends AsyncTask<String,Void,Bitmap> {

    //allowed to be garbaege collected
    private final WeakReference<ImageView> imageViewReference;
    private Bitmap bitmap = null;
    private Context context;
    private String name;


    //Constructor weist Imageview objekt zu
    public Async_ImageView(Context context,ImageView imageView){

        imageViewReference = new WeakReference<ImageView>(imageView);
        this.context = context;

    }

    @Override
    protected Bitmap doInBackground(String... params) {

        name = params[0];
        get_image_reference();
        return bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.d("Async_ImageView", "onPost");

        //sets image to imageview
        if(imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            imageView.setImageBitmap(bitmap);

        }


    }

    private void get_image_reference(){

        File sd_Path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My_Rss_Feed_App/thumbnails/" + name + "_thumbnail.jpg");
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try{
            //falls die thumbnails auf der sdcard sind
            if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED ) && (sd_Path.exists())){
                Log.d("MyAdapter", "loading thumbnail external " + name);
                load_imageANDresize(sd_Path);
            }
            //falls die thumbnails auf dem internen speicher sind
            else if(context.getFileStreamPath(name + "_thumbnail.jpg").exists()){
                Log.d("MyAdapter","loading thumbnail internal" + name);
                load_imageANDresize(context.getFileStreamPath(name + "_thumbnail.jpg"));
            }
            else {
                //falls keine gefunden werden
                Log.d("MyAdapter", "loading placeholder " + name);
                load_placeholder();
            }
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
            load_placeholder();
        }
        catch (RuntimeException e){
            e.printStackTrace();
            load_placeholder();
        }



        return;


    }


    private void load_imageANDresize(File ref){


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //wir öffnen den stream, decoden stream und ohne speicher freizugeben, berechnen benötigte größe
        // anhand der größe des imageviewers
        try {
            BitmapFactory.decodeFile(ref.getAbsolutePath(), options);
            options.inSampleSize = calculateInSampleSize(options,imageViewReference.get().getWidth(),imageViewReference.get().getHeight());
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(ref.getAbsolutePath(), options);

        }


        catch (NullPointerException e) {
            Log.d("MyAdapter", "loading placeholder " + name);
            e.printStackTrace();
            load_placeholder();

            //bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.placeholder);
            //holder.imageView.setImageResource(R.drawable.placeholder);

        }


        return;
    }

    private boolean load_placeholder(){


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try{
            BitmapFactory.decodeResource(context.getResources(),R.drawable.placeholder_medium_100x70,options);
            options.inSampleSize = calculateInSampleSize(options,imageViewReference.get().getWidth(),imageViewReference.get().getHeight());
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.placeholder_medium_100x70,options);

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        return true;

    }


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


        return inSampleSize;
    }



}
