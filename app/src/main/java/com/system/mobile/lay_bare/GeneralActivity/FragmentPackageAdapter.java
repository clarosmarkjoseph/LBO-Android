package com.system.mobile.lay_bare.GeneralActivity;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.StringHandler;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ITDevJr1 on 4/25/2017.
 */
public class FragmentPackageAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<String> id;
    private ArrayList<String> name;
    private ArrayList<String> desc;
    private ArrayList<String> price_male;
    private ArrayList<String> price_female;
    private ArrayList<String> images;
    DataHandler handler;
    Snackbar snackbar;

    JSONArray jArray = new JSONArray();
    Utilities utilities;
    String SERVER_URL = "";
    String gender;
    ImageView imageView;

    public FragmentPackageAdapter(FragmentActivity activity, JSONArray jsonArray, String gender) {
        this.mContext = activity;
        this.jArray   = jsonArray;
        this.utilities = new Utilities(activity);
        this.gender     = gender;
        SERVER_URL = utilities.returnIpAddress();
    }

    @Override
    public int getCount() {
        return jArray.length();
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
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


            StringHandler stringHandler = new StringHandler();
            JSONObject jsonObject       = jArray.getJSONObject(position);

            String package_id           = jsonObject.getString("service_package_id");
            String itemID               = jsonObject.getString("id");
            String name                 = jsonObject.getString("package_name");
            String desc                 = jsonObject.getString("package_desc");
            String price                = jsonObject.getString("package_price");
            String duration             = jsonObject.getString("package_duration");
            String package_service      = jsonObject.getString("package_services");
            String image                = jsonObject.getString("package_image");
            String package_gender       = jsonObject.getString("package_gender");

            lblName.setText(name);
            final String imageUrl          = SERVER_URL+"/images/services/"+stringHandler.replaceSpaceToCharacter(image);
            lblDesc.setText(Html.fromHtml(utilities.displayConcatDot(desc)));
            lblPrice.setText("₱ "+utilities.convertToCurrency(price));
            lblDuration.setText(duration+" minutes");
            utilities.setUniversalSmallImage(imageView,imageUrl);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return v;

    }



}
