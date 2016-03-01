package com.example.macbook.nogridtestapp.my_fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.macbook.nogridtestapp.async_classes.Async_URLTest;
import com.example.macbook.nogridtestapp.my_interface.Add_Dialog_Interface;

import com.example.macbook.nogridtestapp.R;
import com.example.macbook.nogridtestapp.my_interface.Async_URLTest_Interface;

/**
 * url test fehlt, namen filter fehlt
 */



public class Add_Dialog_Fragment extends DialogFragment implements View.OnClickListener,Async_URLTest_Interface{

    private EditText name;
    private EditText url;
    private Button ok;
    private Button cancel;
    public  Add_Dialog_Interface dialog_interface = null;


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialog_interface = (Add_Dialog_Interface) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        Log.d("Add_Dialog_Fragment","#######got called");

        //set view und überschrift des dialogs
        View view = inflater.inflate(R.layout.add_dialog_layout, container);
        getDialog().setTitle(R.string.add_dialgog_text);

        //beide textfelder
        name = (EditText) view.findViewById(R.id.edit_dialog_name);
        url = (EditText) view.findViewById(R.id.edit_dialog_url);

        name.requestFocus();

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        //beide button
        ok = (Button) view.findViewById(R.id.button_dialog_ok);
        cancel = (Button) view.findViewById(R.id.button_dialog_cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {

        Log.d("Add_Dialog_Fragment","#######onClick got called");

        System.out.print(String.valueOf(v.getId()));
        if(v.getId()==R.id.button_dialog_ok){

            Async_URLTest urlTest = new Async_URLTest(getActivity());
            urlTest.return_interface = this;
            urlTest.execute(url.getText().toString(),name.getText().toString());



        }

        if(v.getId()==R.id.button_dialog_cancel){

            this.dismiss();
        }
    }



    @Override
    public void url_test_result(Bundle connection) {

        if(connection.getBoolean("connect")){

            String con_url,con_name,description,con_image;

            con_url = connection.getString("url");
            con_image = connection.getString("image");
            description = connection.getString("description");
            con_name = connection.getString("name");
            //System.out.println(con_name);

            if(con_name == "" || name.getText().toString() == "" || name.getText().toString() == "Name"){
                //falls kein name eingegeben wurde und keiner gefunden wurde
                con_name = "rss_feed";
            }

            dialog_interface.onFinishAddDialog(con_name, con_url, description, con_image);

            this.dismiss();
        }
        else {
            Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_SHORT).show();

        }
    }
}
