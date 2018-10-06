package com.system.mobile.lay_bare.Location;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by paolohilario on 3/20/18.
 */

public class RecyclerBranchReview extends RecyclerView.Adapter<RecyclerBranchReview.ViewHolder>{

    Context context;
    JSONArray arrayReviews;
    View layout;
    Utilities utilities;
    String SERVER_URL = "";

    public RecyclerBranchReview(Context context, JSONArray arrayReviews){
        this.arrayReviews = arrayReviews;
        this.context      = context;
        this.utilities    = new Utilities(context);
        this.SERVER_URL   = utilities.returnIpAddress();
        Log.e("arrayRviews",arrayReviews.toString());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layout                  = LayoutInflater.from(context).inflate(R.layout.recycler_branch_review, parent, false);
        ViewHolder viewHolder   = new ViewHolder(layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject objectReview = arrayReviews.getJSONObject(position);
            String feedback         = objectReview.optString("feedback","");
            String review_status    = objectReview.optString("review_status","");
            String username         = objectReview.optString("username","Unknown User");
            String first_name       = objectReview.optString("first_name","Unknown User");
            String picture          = objectReview.optString("user_picture","no photo.jpg");
            String created_at       = objectReview.getString("updated_at");
            int rating              = objectReview.optInt("rating",0);
            String timeAgo          = utilities.getTimeAgo(created_at);
//            if(review_status == "pending"){
//
//            }

            String imageUrl         = SERVER_URL+"/images/users/"+picture;
            holder.ratingBar.setRating(rating);
            holder.lblRemarks.setText(feedback);
            holder.lblClientName.setText(username);
            holder.lblDateAgo.setText(timeAgo);
            utilities.setUniversalSmallImage(holder.imgReviewClient,imageUrl);


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayReviews.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgReviewClient;
        TextView lblClientName,lblDateAgo,lblRemarks;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            imgReviewClient = (ImageView)itemView.findViewById(R.id.imgReviewClient);
            lblClientName   = (TextView)itemView.findViewById(R.id.lblClientName);
            lblDateAgo      = (TextView)itemView.findViewById(R.id.lblDateAgo);
            lblRemarks      = (TextView)itemView.findViewById(R.id.lblRemarks);
            ratingBar       = (RatingBar)itemView.findViewById(R.id.ratingBar);

        }
    }
}
