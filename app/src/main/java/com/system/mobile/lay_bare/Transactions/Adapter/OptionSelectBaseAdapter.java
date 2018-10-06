package com.system.mobile.lay_bare.Transactions.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;

import java.util.ArrayList;

/**
 * Created by Mark on 03/10/2017.
 */

public class OptionSelectBaseAdapter extends BaseAdapter{


    Context context;
    ArrayList<String> arrayTitle   = new ArrayList<>();
    ArrayList<String> arrayDesc    = new ArrayList<>();
    ArrayList<Integer> arrayImage  = new ArrayList<>();

    public OptionSelectBaseAdapter(Context ctx,ArrayList<String> title,ArrayList<String> desc,ArrayList<Integer> image){
        this.context    = ctx;
        this.arrayTitle = title;
        this.arrayDesc  = desc;
        this.arrayImage = image;
    }

    @Override
    public int getCount() {
        return arrayTitle.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View list;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            list = inflater.inflate(R.layout.option_select_listview, null);
        } else {
            list = convertView;
        }
        TextView txtTitle         = (TextView) list.findViewById(R.id.txtOptionTitle);
        TextView txtDesc          = (TextView) list.findViewById(R.id.txtOptionDesc);
        ImageView imageView       = (ImageView)list.findViewById(R.id.imgOption);
        imageView.setImageResource(arrayImage.get(position));
        txtTitle.setText(arrayTitle.get(position));
        txtDesc.setText(arrayDesc.get(position));

        return list;

    }
}
