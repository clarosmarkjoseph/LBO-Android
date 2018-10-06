package com.system.mobile.lay_bare.PLC;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.MemoryUtilities;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by paolohilario on 12/29/17.
 */

public class PreviewPLCCardAdapter extends PagerAdapter {

    View layout;
    Context context;
    LayoutInflater layoutInflater;
    Utilities utilities;
    String SERVER_URL;
    JSONObject objectParams;
    int[] imageCardMale     = new int[]{R.drawable.plc_male,R.drawable.plc_male_back};
    int[] imageCardFemale   = new int[]{R.drawable.plc_female,R.drawable.plc_female_back};

    ImageView imgCard;
    private  static final float BYTES_PER_PX = 4.0f;


    public PreviewPLCCardAdapter(Context ctx, JSONObject objectParams) {

        this.context        = ctx;
        this.utilities      = new Utilities(context);
        this.SERVER_URL     = utilities.returnIpAddress();
        this.objectParams   = objectParams;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater      = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout              = layoutInflater.inflate(R.layout.plc_preview_card_adapter,null);
        try {

            imgCard                     = (ImageView)layout.findViewById(R.id.imgCard);
            TextView lblFullName        = (TextView)layout.findViewById(R.id.lblFullName);
            TextView lblFullBranch      = (TextView)layout.findViewById(R.id.lblFullBranch);
            TextView lblFullID          = (TextView)layout.findViewById(R.id.lblFullID);
            TextView lblFullBday        = (TextView)layout.findViewById(R.id.lblFullBday);
            String gender               = objectParams.getString("gender");

            if(gender.equals("male") || gender.equals("Male")){
//                loadImages(imageCardMale[position]);
                imgCard.setImageDrawable(context.getResources().getDrawable(imageCardFemale[position]));
            }
            else{
//                loadImages(imageCardFemale[position]);
                imgCard.setImageDrawable(context.getResources().getDrawable(imageCardFemale[position]));
            }

            if(position==1) {
                lblFullName.setVisibility(View.VISIBLE);
                lblFullBranch.setVisibility(View.VISIBLE);
                lblFullID.setVisibility(View.VISIBLE);
                lblFullBday.setVisibility(View.VISIBLE);
                lblFullName.setText(objectParams.getString("full_name"));
                lblFullBranch.setText(objectParams.getString("branch"));
                lblFullID.setText(objectParams.getString("reference_no"));
                lblFullBday.setText(utilities.getCompleteDateMonth(objectParams.optString("bday","0000-00-00")));
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        ViewPager vp = (ViewPager) container;
        vp.addView(layout, 0);
        return layout;
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view    = (View)object;
        vp.removeView(view);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return "TAB " + (position + 1);
    }
//

}
