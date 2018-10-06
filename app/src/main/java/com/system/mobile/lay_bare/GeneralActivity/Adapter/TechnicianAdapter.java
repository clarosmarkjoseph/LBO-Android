package com.system.mobile.lay_bare.GeneralActivity.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.content.Context;
import android.widget.TextView;
import android.util.Log;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Mark on 04/10/2017.
 */

public class TechnicianAdapter extends BaseAdapter{

    JSONArray jsonArray = new JSONArray();
    Context context;
    TextView lblBranch;
    TextView lblDesc;
    Utilities utilities;
    public TechnicianAdapter(Context ctx,JSONArray jArray) {
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
            Log.e("OBJECTS ",jsonObject.toString());
            String id               = jsonObject.getString("id");
            String branch_name      = jsonObject.getString("name");
            JSONObject jsonObject1  = jsonObject.getJSONObject("schedule");
            String sched_start      = jsonObject1.getString("start");
            String sched_end        = jsonObject1.getString("end");
            utilities = new Utilities(context);
            lblBranch.setText(branch_name);
            lblDesc.setText(utilities.convert12Hours(sched_start)+" - "+utilities.convert12Hours(sched_end));
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
