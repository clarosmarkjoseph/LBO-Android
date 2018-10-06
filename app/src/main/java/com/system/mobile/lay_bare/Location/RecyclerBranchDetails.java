package com.system.mobile.lay_bare.Location;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mark on 04/12/2017.
 */

public class RecyclerBranchDetails extends  RecyclerView.Adapter<RecyclerBranchDetails.ViewHolder>{

    View view;
    Context context;
    JSONObject jsonObject;
    String contact_no = "";
    boolean isExpanded = false;
    LocationClassSingleton locationClassSingleton;
    Utilities utilities;


    public RecyclerBranchDetails(Context context){
        this.context                = context;
        this.locationClassSingleton = new LocationClassSingleton().Instance();
        this.jsonObject             = locationClassSingleton.getBranchDetails();
        this.utilities              = new Utilities(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.location_overview_recycler, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String currentDate = utilities.getCurrentDateTime();
        if (jsonObject.length() > 0) {
            try {
                String branch_id                = jsonObject.getString("id");
                String welcome_message          = jsonObject.getString("welcome_message");
                String branch_name              = jsonObject.getString("branch_name");
                String branch_address           = jsonObject.getString("branch_address");
                String branch_contact           = jsonObject.getString("branch_contact");
                String branch_email             = jsonObject.getString("branch_email");
                String branch_contact_person    = jsonObject.getString("branch_contact_person");
                String payment_methods          = jsonObject.getString("payment_methods");
                JSONArray arraySchedule         = jsonObject.getJSONArray("schedules");
                String timeElement              = "";
                String weekElement              = "";
                String timeDisplay              = "";
                boolean isAlready               = false;

                if((welcome_message.equals(null)) || welcome_message.equals("null") || welcome_message.equals("") || welcome_message.isEmpty() || welcome_message.length() <= 0 ){
                    holder.lblDescription.setText("Welcome to Lay Bare "+ branch_name);
                }
                else{
                    holder.lblDescription.setText(welcome_message);
                }

                for (int x = 0; x < arraySchedule.length(); x++){

                    JSONObject objectSchedule = arraySchedule.getJSONObject(x);
                    String schedule_type      = objectSchedule.getString("schedule_type");
                    String date_start         = objectSchedule.getString("date_start");
                    String date_end           = objectSchedule.getString("date_end");
                    JSONArray arraySched      = objectSchedule.getJSONArray("schedule_data");
                    int countWeek             = utilities.getDayOfWeek(null);
//                    int start_position        = utilities.getDateWeekPosition(utilities.removeTimeFromDate(date_start));
//                    int end_position          = utilities.getDateWeekPosition(utilities.removeTimeFromDate(date_end));
                    holder.lblSchedule.setText("The Branch is OPEN today");
                    holder.lblSchedule.setTextColor(context.getResources().getColor(R.color.laybareGreen));
                    String[] newDays = new String[]{"", "", "","","","",""};
                    for (int a = 0; a < arraySched.length(); a++) {
                        JSONObject objectData     = arraySched.getJSONObject(a);
                        String start              = utilities.convert12Hours(objectData.getString("start"));
                        String end                = utilities.convert12Hours(objectData.getString("end"));

                        newDays[a]                = utilities.getWeekString(a) + "";

                        timeElement = start+" - "+end+"\n";
                        weekElement = newDays[a]+"\n";;

                        LinearLayout linearLayout = new LinearLayout(context);

                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setWeightSum(1.0f);

                        LinearLayout.LayoutParams paramWeeks = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        paramWeeks.weight = 0.55f;

                        LinearLayout.LayoutParams paramTime = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        paramTime.weight = 0.45f;

                        final TextView lblTxtWeek = new TextView(context);
                        final TextView lblTxtTime = new TextView(context);
                        lblTxtWeek.setLayoutParams(paramWeeks);
                        lblTxtTime.setLayoutParams(paramTime);

                        lblTxtTime.setTextColor(context.getResources().getColor(R.color.themeBlack));
                        lblTxtWeek.setTextColor(context.getResources().getColor(R.color.themeBlack));

                        lblTxtWeek.setText(weekElement);
                        lblTxtTime.setText(timeElement);

                        linearLayout.addView(lblTxtWeek);
                        linearLayout.addView(lblTxtTime);
                        holder.linear_sched_data.addView(linearLayout);
                    }

                    if(utilities.checkDateRange(date_start,date_end) == true && schedule_type.equals("closed")){
                        holder.lblSchedule.setText("The Branch is CLOSED today");
                        holder.lblSchedule.setTextColor(context.getResources().getColor(R.color.themeRed));
                        break;
                    }
                }
                setContactNumber(branch_contact);
                holder.lblAddress.setText(branch_address);
                holder.lblContact.setText(branch_contact);
                holder.lblEmail.setText(branch_email);
                holder.lblContactPerson.setText(branch_contact_person);
                holder.lblPayment.setText(payment_methods);
                holder.lblWeb.setText("None");
                holder.linear_rowSchedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isExpanded == false){
                            isExpanded = true;
                            holder.expandableLayout.expand();
                            holder.lblSchedule.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                        }
                        else{
                            isExpanded = false;
                            holder.lblSchedule.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                            holder.expandableLayout.collapse();
                        }
                    }
                });
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblDescription,lblAddress,lblEmail,lblSchedule,lblContact,lblContactPerson,lblPayment,lblWeb;
        ExpandableRelativeLayout expandableLayout;
        LinearLayout linear_schedule,linear_rowSchedule,linear_sched_data;

        public ViewHolder(final View itemView) {
            super(itemView);

            lblDescription      = (TextView) itemView.findViewById(R.id.lblDescription);
            lblAddress          = (TextView) itemView.findViewById(R.id.lblAddress);
            lblEmail            = (TextView) itemView.findViewById(R.id.lblEmail);
            lblSchedule         = (TextView) itemView.findViewById(R.id.lblSchedule);
            lblContact          = (TextView) itemView.findViewById(R.id.lblContact);
            lblContactPerson    = (TextView) itemView.findViewById(R.id.lblContactPerson);
            lblPayment          = (TextView) itemView.findViewById(R.id.lblPayment);
            lblWeb              = (TextView) itemView.findViewById(R.id.lblWeb);
            expandableLayout    = (ExpandableRelativeLayout)itemView.findViewById(R.id.expandableLayout);
            linear_schedule     = (LinearLayout) itemView.findViewById(R.id.linear_schedule);
            linear_rowSchedule  = (LinearLayout) itemView.findViewById(R.id.linear_rowSchedule);
            linear_sched_data   = (LinearLayout) itemView.findViewById(R.id.linear_sched_data);



        }
    }

    public void setContactNumber(String contact_no1){
        this.contact_no = contact_no1;
    }

    public String getContactNumber(){
        return this.contact_no;
    }


}