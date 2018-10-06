package com.system.mobile.lay_bare.NavigationDrawer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Mark on 17/11/2017.
 */

public class ViewPagerAdapter extends PagerAdapter {
    JSONArray arrayBanner;
    Context context;
    Utilities utilities;
    LayoutInflater layoutInflater;
    String SERVER_URL = "";
    String type = "";


    public ViewPagerAdapter(Context ctx,JSONArray arrayBanners,String types) {
        this.arrayBanner = arrayBanners;
        this.context     = ctx;
        this.utilities   = new Utilities(ctx);
        this.SERVER_URL  = utilities.returnIpAddress();
        layoutInflater   = LayoutInflater.from(context);
        this.type        = types;
    }

    @Override
    public int getCount() {
        return arrayBanner.length();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater      = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view      = layoutInflater.inflate(R.layout.fragment_home_adapter_carousel,null);
        try {

            final ImageView imgCarousel = (ImageView)item_view.findViewById(R.id.imgCarousel);
            JSONObject object   = arrayBanner.getJSONObject(position);
            String image        = object.getString("image");
            if(this.type.equals("branches")){
                image  = SERVER_URL+"/images/branches/"+image.replace(" ","%20");
            }
            else{
                image  = SERVER_URL+"/images/ads/"+image.replace(" ","%20");
            }
            final String finalImage = image;
            Picasso.with(context)
                    .load(finalImage)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .noFade()
                    .into(imgCarousel, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError() {
                            Picasso.with(context)
                                    .load(finalImage)
                                    .placeholder(R.drawable.no_image)
                                    .error(R.drawable.no_image)
                                    .noFade()
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .error(R.drawable.no_image)
                                    .into(imgCarousel, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }
                                        @Override
                                        public void onError() {
                                        }
                                    });
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ViewPager vp = (ViewPager) container;
        vp.addView(item_view, 0);
        return item_view;
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
//
//
////        grid.setDrawingCacheQuality(mContext.getResources().getDrawable(R.drawable.circle_transparent));
//        return grid;
//    }
}
