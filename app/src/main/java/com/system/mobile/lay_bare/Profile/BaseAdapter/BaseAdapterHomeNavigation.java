package com.system.mobile.lay_bare.Profile.BaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;

import java.util.ArrayList;

/**
 * Created by Mark on 11/09/2017.
 */

public class BaseAdapterHomeNavigation extends BaseAdapter {


    private Context mContext;
    private ArrayList<String> label;
    private ArrayList<Integer> image;
    private ArrayList<Integer> color;
    RelativeLayout relativeMenuGrid;

    public BaseAdapterHomeNavigation(Context c, RelativeLayout relativeLayout, ArrayList<String> label_constructor, ArrayList<Integer> arrayHome_img, ArrayList<Integer> colors) {
        mContext = c;
        this.label = label_constructor;
        this.image = arrayHome_img;
        this.color = colors;
        relativeMenuGrid = relativeLayout;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return label.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.recycler_navigation_button, null);
        } else {
            grid = convertView;
        }

        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        final ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
        imageView.setImageResource(image.get(position));
        textView.setText(label.get(position));
        grid.setBackgroundColor(mContext.getResources().getColor(color.get(position)));

//        grid.setDrawingCacheQuality(mContext.getResources().getDrawable(R.drawable.circle_transparent));
        return grid;
    }



}

