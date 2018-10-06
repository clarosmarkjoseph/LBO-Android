package com.system.mobile.lay_bare.GeneralActivity;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by personal on 4/28/2017.
 */

public class FragmentProductAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<String> name;
    private ArrayList<String> desc;
    private ArrayList<String> price;
    private ArrayList<String> images;
    DataHandler handler;
    Snackbar snackbar;
    RelativeLayout relativeMenuGrid;
    JSONArray jsonArray = new JSONArray();
    Utilities utilities;
    String SERVER_URL = "";
    ImageView imageView;


    public FragmentProductAdapter(FragmentActivity activity, JSONArray jArrayPackage) {
        this.mContext  = activity;
        this.jsonArray = jArrayPackage;
        this.utilities = new Utilities(this.mContext);
        SERVER_URL = utilities.returnIpAddress();
    }


    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            v = inflater.inflate(R.layout.fragment_service_adapter, null);

        }
        else {
            v = convertView;
        }
        TextView lblName              = (TextView) v.findViewById(R.id.lblName);
        TextView lblDesc              = (TextView) v.findViewById(R.id.lblDesc);
        TextView lblPrice             = (TextView) v.findViewById(R.id.lblPrice);
        imageView                     = (ImageView)v.findViewById(R.id.imgItem);
        TextView lblDuration          = (TextView)v.findViewById(R.id.lblDuration);
        try {
            JSONObject jsonObject       = jsonArray.getJSONObject(position);
            String id                   = jsonObject.getString("id");
            String name                 = jsonObject.getString("product_group_name");
            String desc                 = jsonObject.getString("product_description");
            String size                 = jsonObject.getString("product_size");
            String variant              = jsonObject.getString("product_variant");
            String price                = jsonObject.getString("product_price");
            final String image          = jsonObject.getString("product_picture");
            String label = "";

            lblName.setText(name);
            lblDesc.setText(utilities.displayConcatDot(desc));
            lblPrice.setText("₱ "+utilities.convertToCurrency(price));
            if(!variant.equals("")){
               label+= variant+" ("+size+")";
            }
            else{
                label+=size;
            }

            lblDuration.setText(label);
            final String imgUrl = SERVER_URL+"/images/products/"+image;
            utilities.setUniversalSmallImage(imageView,imgUrl);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return v;

    }
}
