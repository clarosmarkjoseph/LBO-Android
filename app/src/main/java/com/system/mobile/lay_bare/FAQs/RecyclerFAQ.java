package com.system.mobile.lay_bare.FAQs;

import android.app.Service;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 10/11/2017.
 */

public class RecyclerFAQ extends  RecyclerView.Adapter<RecyclerFAQ.ViewHolder> {

    Context context;
    Utilities utilities;
    String SERVER_URL = "";
    View layout;
    InputMethodManager imm;
    JSONArray arrayFAQ;
    boolean isLiked = false;

    public RecyclerFAQ(FragmentActivity activity, JSONArray arrayNav1) {
        this.context = activity;
        this.utilities = new Utilities(activity);
        this.arrayFAQ = arrayNav1;
    }

    @Override
    public RecyclerFAQ.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        layout = LayoutInflater.from(context).inflate(R.layout.faq_recycler_questions, parent, false);
        SERVER_URL = utilities.returnIpAddress();
        ViewHolder vh = new RecyclerFAQ.ViewHolder(layout);
        imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RecyclerFAQ.ViewHolder view = holder;

        try {
            JSONObject jsonObject   = arrayFAQ.getJSONObject(position);
            String question         = jsonObject.getString("question");
            String answer           = jsonObject.getString("answer");

            view.lblQuestion.setText(Html.fromHtml(question));
            view.lblAnswer.setText(Html.fromHtml(answer));


        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return arrayFAQ.length();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblQuestion, lblAnswer;
        public ViewHolder(final View itemView) {
            super(itemView);
            lblQuestion = (TextView) itemView.findViewById(R.id.lblQuestion);
            lblAnswer   = (TextView) itemView.findViewById(R.id.lblAnswer);
        }

    }



}