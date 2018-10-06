package com.system.mobile.lay_bare.GeneralActivity;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.StringHandler;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ITDevJr1 on 4/25/2017.
 */
public class FragmentServiceAdapter extends BaseAdapter{

    private Context mContext;
    JSONArray jArray;
    Utilities utilities;
    String SERVER_URL = "";
    ImageView imageView;
    String gender;

    public FragmentServiceAdapter(FragmentActivity activity, JSONArray jsonArray, String gender) {
        this.mContext   = activity;
        this.jArray     = jsonArray;
        this.utilities  = new Utilities(activity);
        this.gender     = gender;
        SERVER_URL      = utilities.returnIpAddress();
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
            JSONObject jsonObject          = jArray.getJSONObject(position);
            String service_id              = jsonObject.getString("id");
            String service_name            = jsonObject.getString("service_name");
            String service_desc            = jsonObject.getString("service_description");
            String service_price           = jsonObject.getString("service_price");
            String duration                = jsonObject.getString("service_minutes");
            String service_image           = jsonObject.getString("service_picture");
            String service_gender          = jsonObject.getString("service_gender");

            if(gender == null){
                lblName.setText(service_name+" ("+utilities.capitalize(service_gender)+")");
            }
            else{
                lblName.setText(service_name);
            }

            if((service_price.equals("")) || (service_price.equals("0.00"))){
                lblPrice.setText("₱ 0.00");
            }
            else{
                lblPrice.setText("₱ "+utilities.convertToCurrency(service_price));
            }
            lblDesc.setText(Html.fromHtml(utilities.displayConcatDot(service_desc)));
            lblDuration.setText(duration+" minutes");
            StringHandler stringHandler = new StringHandler();
            final String imgUrl = SERVER_URL+"/images/services/"+stringHandler.replaceSpaceToCharacter(service_image);
            utilities.setUniversalSmallImage(imageView,imgUrl);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return v;

    }
}
