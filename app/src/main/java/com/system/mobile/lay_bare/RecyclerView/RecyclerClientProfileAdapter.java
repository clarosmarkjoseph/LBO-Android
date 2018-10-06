package com.system.mobile.lay_bare.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;

import java.util.ArrayList;

/**
 * Created by Mark on 14/09/2017.
 */

public class RecyclerClientProfileAdapter extends RecyclerView.Adapter<RecyclerClientProfileAdapter.ViewHolder> {

    Context context;
    ArrayList<String> arrayDescription;
    ArrayList<String> arrayContent;
    View view;
    ViewHolder viewHolder;
    RelativeLayout relative_clientprofile;

    public RecyclerClientProfileAdapter(Context ctx, ArrayList<String>arrayDescription1, ArrayList<String>arrayContent1){
        this.context            = ctx;
        this.arrayContent       = arrayContent1;
        this.arrayDescription   = arrayDescription1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.client_profile_cardview,parent,false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //casting couch the func
        holder.lblProfile_Title.setText(arrayDescription.get(position));
        holder.lblProfile_Content.setText(arrayContent.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayContent.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblProfile_Title;
        TextView lblProfile_Content;

        public ViewHolder(final View itemView) {
            super(itemView);
            lblProfile_Title     = (TextView)itemView.findViewById(R.id.lblProfile_Title);
            lblProfile_Content   = (TextView)itemView.findViewById(R.id.lblProfile_Content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    Log.e("HAHA GG","OO NGA");
                }
            });
        }
    }

}


