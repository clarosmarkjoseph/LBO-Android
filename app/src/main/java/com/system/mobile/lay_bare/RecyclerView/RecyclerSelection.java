package com.system.mobile.lay_bare.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Transactions.TimeRecyclerPositionInterface;

import java.util.ArrayList;

/**
 * Created by paolohilario on 3/2/18.
 */

public class RecyclerSelection extends RecyclerView.Adapter<RecyclerSelection.ViewHolder> {
    ArrayList<String> arraySchedule;
    Context context;
    View view;
    ViewHolder viewHolder;
    int index = 0;
    TextView lblHour,lblMinute;
    Button btnNext;

    public RecyclerSelection(Context ctx, ArrayList<String> arraySchedule, TextView lblHour, TextView lblMinute, int selectedIndex, Button btnNext){
        this.context            = ctx;
        this.arraySchedule      = arraySchedule;
        this.lblHour            = lblHour;
        this.lblMinute          = lblMinute;
        this.index              = selectedIndex;
        this.btnNext            = btnNext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.recycler_selection,parent,false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final int myPos     = position;
        if(index == position){
            holder.btnTime.setBackground(context.getResources().getDrawable(R.drawable.btn_laybare_brown_content));
            holder.btnTime.setTextColor(context.getResources().getColor(R.color.themeWhite));
            holder.btnTime.setFocusable(true);
            holder.btnTime.setFocusableInTouchMode(true);///add this line
            holder.btnTime.requestFocus();
        }
        else {
            holder.btnTime.setBackground(context.getResources().getDrawable(R.drawable.btn_laybare_brown_border_only));
            holder.btnTime.setTextColor(context.getResources().getColor(R.color.brownLoading));
        }

        final String time   = arraySchedule.get(position).toString();
        holder.btnTime.setText(time);
        holder.btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = myPos;
                String initTime = time;
                String[] selectedTime = initTime.split(":");
                lblHour.setText(selectedTime[0]);
                lblMinute.setText(selectedTime[1]);
                notifyDataSetChanged();
                btnNext.setEnabled(true);
                btnNext.setAlpha(1);
                TimeRecyclerPositionInterface timeRecyclerPositionInterfaces  = (TimeRecyclerPositionInterface) context;
                timeRecyclerPositionInterfaces.setIndex(position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return arraySchedule.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        Button btnTime;

        public ViewHolder(final View itemView) {
            super(itemView);
            btnTime     = (Button) itemView.findViewById(R.id.btnTime);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int index = getAdapterPosition();
//                    Log.e("INDEX",String.valueOf(index));
//                }
//            });
        }
    }
}
