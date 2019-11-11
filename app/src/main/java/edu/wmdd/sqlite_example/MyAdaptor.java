package edu.wmdd.sqlite_example;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.MyViewHolder> {

    private List<String> mTitle;

    private List<String> mUrl;
    private Context mcon;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public LinearLayout linearLayout;

        public MyViewHolder(View v) {

            super(v);
            textView = v.findViewById(R.id.my_text_view);
            linearLayout = v.findViewById(R.id.linearlayout);

        }
    }


    public MyAdaptor(Context con, List<String> myDataset, List<String> myUrl ) {

        mTitle = myDataset;

        mUrl = myUrl;
        mcon = con;

    }






    @Override
    public MyAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);



        MyViewHolder vh = new MyViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {



        holder.textView.setText(mTitle.get(position));

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent("message");
                i.putExtra("url", mUrl.get(position).toString());

                // sending message to the other activity using broadcast


                LocalBroadcastManager.getInstance(mcon).sendBroadcast(i);


                Log.d("posn",""+mUrl.get(position));


            }
        });



    }


    public int getItemCount() {

        return mTitle.size();

    }}
