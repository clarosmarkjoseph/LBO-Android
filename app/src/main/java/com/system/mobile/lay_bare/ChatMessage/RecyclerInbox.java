package com.system.mobile.lay_bare.ChatMessage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.SingletonGlobal;
import com.system.mobile.lay_bare.Sockets.SocketApplication;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import io.socket.client.Socket;

/**
 * Created by paolohilario on 4/13/18.
 */

public class RecyclerInbox extends RecyclerView.Adapter<RecyclerInbox.ViewHolder> implements Runnable {

    Context context;
    View layout;
    Utilities utilities;
    String clientID = "";
    ArrayList<JSONObject> arrayListInbox;

    public RecyclerInbox(Context context, ArrayList<JSONObject> arrayListInbox){
        this.arrayListInbox = arrayListInbox;
        this.context        = context;
        this.utilities      = new Utilities(context);
        this.clientID       = utilities.getClientID();
    }

    @Override
    public RecyclerInbox.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layout                  = LayoutInflater.from(context).inflate(R.layout.recycler_chat_message, parent, false);
        ViewHolder viewHolder   = new ViewHolder(layout);
        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(final RecyclerInbox.ViewHolder holder, int position) {

        try{
            String statement   = "";
            JSONObject objectInbox          = arrayListInbox.get(position);
            final String thread_name        = objectInbox.optString("thread_name","");
            String dateTime                 = objectInbox.optString("dateTime","");
            final JSONArray arrayMessage    = objectInbox.getJSONArray("arrayMessage");

            for (int x = 0; x < arrayMessage.length(); x++) {
                JSONObject objectMessage    = null;
                try {
                    objectMessage   = arrayMessage.getJSONObject(x);
                    String body     = objectMessage.getString("body");
                    int sender_id   = objectMessage.getInt("sender_id");
                    String read_at  = objectMessage.getString("read_at");

                    if (sender_id == Integer.parseInt(clientID)) {
                        statement = "You: " + body;
                        holder.lblThreadName.setTextColor(context.getResources().getColor(R.color.themeGray));
                        holder.lblThreadName.setTypeface(Typeface.DEFAULT);
                        holder.lblLastMessage.setTypeface(Typeface.DEFAULT);
                        holder.lblLastMessage.setTextColor(context.getResources().getColor(R.color.themeGray));
                    }
                    else {

                        statement = thread_name + ": " + body;
                        if(x == arrayMessage.length() - 1){
                           if(read_at.equals("0000-00-00") || read_at.equals("null")){
                                holder.lblThreadName.setTextColor(context.getResources().getColor(R.color.themeBlack));
                                holder.lblThreadName.setTypeface(Typeface.DEFAULT_BOLD);
                                holder.lblLastMessage.setTypeface(Typeface.DEFAULT_BOLD);
                                holder.lblLastMessage.setTextColor(context.getResources().getColor(R.color.themeBlack));
                            }
                            else{

                                holder.lblThreadName.setTextColor(context.getResources().getColor(R.color.themeGray));
                                holder.lblThreadName.setTypeface(Typeface.DEFAULT);
                                holder.lblLastMessage.setTypeface(Typeface.DEFAULT);
                                holder.lblLastMessage.setTextColor(context.getResources().getColor(R.color.themeGray));
                            }
                        }
                    }

                    holder.lblLastMessage.setText(statement);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            holder.lblThreadName.setText(thread_name);
            holder.lblDateTime.setText(utilities.formatDatetimeAnyThing(dateTime,"MMM dd yy"));



        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        DateFormat formatTime = DateFormat.getTimeInstance();
        return formatTime.format(calendar.getTime());
    }



    @Override
    public int getItemCount() {
        return arrayListInbox.size();
    }

    @Override
    public void run() {

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView lblLastMessage,lblDateTime,lblThreadName;
        ImageView imgProfile;
        LinearLayout linearInbox;


        public ViewHolder(View itemView) {
            super(itemView);

            lblLastMessage  = (TextView)itemView.findViewById(R.id.lblLastMessage);
            lblDateTime     = (TextView)itemView.findViewById(R.id.lblDateTime);
            lblThreadName   = (TextView)itemView.findViewById(R.id.lblThreadName);
            imgProfile      = (ImageView)itemView.findViewById(R.id.imgProfile);
            linearInbox     = (LinearLayout) itemView.findViewById(R.id.linearInbox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        JSONObject objectInbox      = arrayListInbox.get(getAdapterPosition());
                        int thread_id               = objectInbox.optInt("id",0);
                        String thread_name          = objectInbox.optString("thread_name","No User");
                        int created_by_id           = objectInbox.optInt("created_by_id",0);
                        String participants         = objectInbox.optString("participants","");
                        int user_id                 = 0;


                        JSONArray arrayParticipants = new JSONArray(participants);

                        for (int x = 0; x < arrayParticipants.length(); x++){
                            int part_id = arrayParticipants.getInt(x);
                            if(part_id == Integer.parseInt(clientID)){
                                user_id = created_by_id;
                            }
                            else{
                                user_id =  part_id;
                            }
                        }
                        Intent intent = new Intent(context,ChatMessage.class);
                        intent.putExtra("recipient_id",user_id);
                        intent.putExtra("userName",thread_name);
                        intent.putExtra("thread_id",thread_id);
                        intent.putExtra("chat_type","inbox");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
