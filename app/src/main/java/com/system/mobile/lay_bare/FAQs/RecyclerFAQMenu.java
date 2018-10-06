package com.system.mobile.lay_bare.FAQs;
import android.content.Intent;
import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 16/11/2017.
 */


public class RecyclerFAQMenu extends  RecyclerView.Adapter<RecyclerFAQMenu.ViewHolder> {

    Context context;
    Utilities utilities;
    String SERVER_URL = "";
    View layout;
    InputMethodManager imm;
    JSONArray arrayFAQ;
    JSONArray arrayQuestion;
    JSONArray arrayFilteredQuestions;
    boolean isLiked = false;
    DataHandler handler;

    public RecyclerFAQMenu(FragmentActivity activity, JSONArray arrayCategory) {
        this.context        = activity;
        this.utilities      = new Utilities(activity);
        this.arrayFAQ       = arrayCategory;
        this.arrayQuestion  = new JSONArray();
        this.arrayFilteredQuestions = new JSONArray();
        this.handler        = new DataHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        layout      = LayoutInflater.from(context).inflate(R.layout.faq_recycler_menu, parent, false);
        SERVER_URL  = utilities.returnIpAddress();
        ViewHolder vh = new ViewHolder(layout);
        imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ViewHolder view = holder;

        try {
            JSONObject jsonObject = arrayFAQ.getJSONObject(position);
            final String title          = jsonObject.getString("title");
            String image          = SERVER_URL+"/"+jsonObject.getString("image");
            final int category_id = jsonObject.getInt("category_id");
            view.lblTitle.setText(Html.fromHtml(title));
            utilities.setUniversalSmallImage(view.imgFaqMenu,image);
            view.cardView_FAQ_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,FAQuestions.class);
                    intent.putExtra("category_id",String.valueOf(category_id));
                    intent.putExtra("category_name","About "+title);
                    context.startActivity(intent);
                }


            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return arrayFAQ.length();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblTitle;
        ImageView imgFaqMenu;
        CardView cardView_FAQ_menu;
        public ViewHolder(final View itemView) {
            super(itemView);
            lblTitle            = (TextView) itemView.findViewById(R.id.lblTitle);
            imgFaqMenu          = (ImageView) itemView.findViewById(R.id.imgFaqMenu);
            cardView_FAQ_menu   = (CardView)itemView.findViewById(R.id.cardView_FAQ_menu);
        }

    }

}
