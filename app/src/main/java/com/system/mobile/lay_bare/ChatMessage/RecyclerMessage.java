package com.system.mobile.lay_bare.ChatMessage;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by paolohilario on 3/23/18.
 */

public class RecyclerMessage extends  RecyclerView.Adapter<RecyclerMessage.ViewHolder> {

    Context context;
    ArrayList<JSONObject> arrayListChat;
    View layout;
    DataHandler handler;
    Utilities utilities;
    int clientID;
    String previousDate = "";

    public RecyclerMessage(Context context, ArrayList<JSONObject> arrayChat, int senderID){
        this.context = context;
        this.handler        = new DataHandler(context);
        this.utilities      = new Utilities(context);
        this.clientID       = senderID;
        this.arrayListChat  = arrayChat;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layout                  = LayoutInflater.from(context).inflate(R.layout.recycler_chat, parent, false);
        ViewHolder viewHolder   = new ViewHolder(layout);
        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
//        Log.e("getItemID", String.valueOf(position));
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        try {

            JSONObject objectChat   = arrayListChat.get(position);
            int chatID              = objectChat.getInt("chatID");
            int sender_id           = objectChat.getInt("senderID");
            String body             = objectChat.getString("body");
            String dateTime         = objectChat.optString("dateTime","0000-00-00 00:00:00");
            final String status     = objectChat.getString("status");

            holder.lblTime.setText(utilities.returnTimeAgo(dateTime));
            if (previousDate.equals("")){
                holder.lblTime.setVisibility(View.VISIBLE);
            }
            else{

                if(utilities.getDateTimeDifference(previousDate,dateTime,"minutes") >= 10){
                    holder.lblTime.setVisibility(View.VISIBLE);
                }
                else{
                    if(position % 5 == 0){
                        holder.lblTime.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.lblTime.setVisibility(View.GONE);
                    }
                }
            }
            previousDate = dateTime;
            if(clientID == sender_id){
                holder.linearRecipient.setVisibility(View.GONE);
                holder.linearSender.setVisibility(View.VISIBLE);
                if(status.equals("sending")){
                    holder.linearSender.setAlpha((float) 0.5);
                    holder.imgViewIfSeen.setVisibility(View.VISIBLE);
                    holder.imgViewIfSeen.setBackground(context.getResources().getDrawable(R.drawable.circle_small_brown_border_only));
                }
                if(status.equals("sent")){
                    holder.linearSender.setAlpha(1);
                    holder.imgViewIfSeen.setVisibility(View.VISIBLE);
                    holder.imgViewIfSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.check));
                    holder.imgViewIfSeen.setBackground(context.getResources().getDrawable(R.drawable.circle_small_brown));
                    holder.imgViewIfSeen.setColorFilter(context.getResources().getColor(R.color.themeWhite));
                }
                if(status.equals("failed")){
                    holder.linearSender.setAlpha((float) 0.5);
                    holder.imgViewIfSeen.setVisibility(View.VISIBLE);
                    holder.imgViewIfSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.a_exclamation));
                    holder.imgViewIfSeen.setColorFilter(context.getResources().getColor(R.color.themeRed));
                    holder.imgViewIfSeen.setBackground(context.getResources().getDrawable(R.drawable.circle_small_red_border_only));
                }
                if(status.equals("seen")){
                    holder.imgViewIfSeen.setVisibility(View.GONE);
                }
                holder.linearChat.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (status.equals("failed")){
                            Log.e("Click position", String.valueOf(position));
                            showPopUpOptions(status,holder,position);
                        }
                        return false;
                    }
                });
                holder.lblChatSenderBody.setText(body);
            }
            else{
                holder.linearSender.setVisibility(View.GONE);
                holder.linearRecipient.setVisibility(View.VISIBLE);
                holder.lblChatRecipientBody.setText(body);
            }



            holder.linearChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.lblTime.getVisibility() == View.VISIBLE){
                        holder.lblTime.setVisibility(View.GONE);
                    }
                    else{
                        holder.lblTime.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void showPopUpOptions(final String status, final ViewHolder holder, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Options");
        String[]  itemsSent = new String[]{"Retry"};
        final String[] finalItemsSent = itemsSent;
        builder.setItems(itemsSent, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemClicked) {
                String optionSelected = finalItemsSent[itemClicked];
                if(status.equals("failed")){
                    InterfaceForChat requestActionFromRecyclerInterface = (InterfaceForChat) context;
                    requestActionFromRecyclerInterface.requestAction(position,"retry");
                }
            }
        });
        AlertDialog myDialog = builder.create();
        myDialog.show();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView lblTime,lblChatRecipientBody,lblChatSenderBody;
        LinearLayout linearSender,linearRecipient,linearChat;
        ImageView imgViewIfSeen,imgRecipient;

        public ViewHolder(View itemView) {
            super(itemView);

            lblTime              = (TextView)itemView.findViewById(R.id.lblTime);
            lblChatRecipientBody = (TextView)itemView.findViewById(R.id.lblChatRecipientBody);
            lblChatSenderBody    = (TextView)itemView.findViewById(R.id.lblChatSenderBody);
            linearChat           = (LinearLayout)itemView.findViewById(R.id.linearChat);
            linearSender         = (LinearLayout)itemView.findViewById(R.id.linearSender);
            linearRecipient      = (LinearLayout)itemView.findViewById(R.id.linearRecipient);
            imgViewIfSeen        = (ImageView) itemView.findViewById(R.id.imgViewIfSeen);
            imgRecipient         = (ImageView) itemView.findViewById(R.id.imgRecipient);

        }
    }

    @Override
    public int getItemCount() {
        return arrayListChat.size();
    }


}
