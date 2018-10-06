package com.system.mobile.lay_bare.GeneralActivity.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.content.Context;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Mark on 04/10/2017.
 */

public class BranchAdapter extends BaseAdapter{

    JSONArray jsonArray = new JSONArray();

    Context context;
    TextView lblBranch;
    TextView lblDesc;
    public BranchAdapter(Context ctx,JSONArray jArray) {
        this.context   = ctx;
        this.jsonArray = jArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.branch_selection_adapter, null);
        } else {
            view = convertView;
        }
        lblBranch        = (TextView) view.findViewById(R.id.lblBranch);
        lblDesc          = (TextView) view.findViewById(R.id.lblDesc);

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            String id           = jsonObject.getString("id");
            String branch_name  = jsonObject.getString("branch_name");
            String desc         = jsonObject.getString("branch_address");
            lblBranch.setText(branch_name);
            lblDesc.setText(desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
        return view;


    }


    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }






}
