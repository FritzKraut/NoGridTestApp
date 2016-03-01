package com.example.macbook.nogridtestapp.async_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by macbook on 9/2/15.
 *
 *
 *
 *
 *
 * Speichert Thumbnails der BlogLogos auf den internen Speicher
 *
 *
 *
 */


public class Async_Thumbnails extends AsyncTask<String, Void, Void> {


    //wichtig für das schreiben auf den internen speicher
    private Context context;

    public Async_Thumbnails (Context context) {
        this.context = context;
    }



    @Override
    protected Void doInBackground(String... params) {

        /*
        * params[0] ist der blogname,
        * params[1] ist dir url des bildes
        * */



        Log.d("Asynx_Thumbnail", "got called");

        String blog_name,blog_image_url;
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/My_Rss_Feed_App/thumbnails";
        File directory = new File(fullPath);


        //if app directory not exist and sdcard mounted
        if(!directory.exists() && isSDPresent){
            Log.d("Asynx_Thumbnail", "directory does not exist");
            directory.mkdirs();
        }

        Log.d("Asynx_Thumbnail", "Path: " + String.valueOf(directory.exists()));


        for(int i=0;i<params.length;i+=2) {

            blog_name = params[i];
            blog_image_url = params[i+1];
            Bitmap bitmap;

            try {

                //get file
                URL url = new URL(blog_image_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                inputStream.mark(0);

                //wir öffnen den stream, decoden stream und ohne speicher freizugeben, berechnen benötigte größe
                // anhand der größe des imageviewers
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);

                //Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, 130, 90);

                //inputStream muss geresetet werden, da er vorher einmal durchgelaufen ist, sonst retrunt der stream null
                inputStream.reset();
                options.inJustDecodeBounds = false;
                // Decode bitmap with inSampleSize set
                bitmap = BitmapFactory.decodeStream(inputStream,null,options);

                inputStream.close();
                httpURLConnection.disconnect();



                FileOutputStream stream_out;

                //safe files
                if(isSDPresent) {
                    File thumbnail = new File(fullPath + "/" + blog_name + "_thumbnail.jpg");

                    stream_out = new FileOutputStream(thumbnail);
                    //save bitmap to SD
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream_out);
                    stream_out.close();
                    Log.d("Async_Thumbnails", blog_name + "_thumbnail.jpg: " + String.valueOf(thumbnail.exists()));


                }
                else {
                    stream_out =  context.openFileOutput(blog_name + "_thumbnail.jpg", Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream_out);
                    stream_out.close();
                    Log.d("Async_Thumbnails", blog_name + "_thumbnail.jpg: " + String.valueOf(context.getFileStreamPath(blog_name + "_thumbnail.jpg").exists()));

                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
            e.printStackTrace();
        }
        }
        return null;
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

        return inSampleSize;
    }



}
