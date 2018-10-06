package com.system.mobile.lay_bare.NavigationDrawer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.system.mobile.lay_bare.Location.LocationIndex;
import com.system.mobile.lay_bare.Queuing.Queuing;
import com.system.mobile.lay_bare.Transactions.AppointmentForm;
import com.system.mobile.lay_bare.Transactions.AppointmentSingleton;
import com.system.mobile.lay_bare.GeneralActivity.ServicePackageProduct;
import com.system.mobile.lay_bare.FAQs.FAQ;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

/**
 * Created by Mark on 10/11/2017.
 */


public class RecyclerButton extends RecyclerView.Adapter<RecyclerButton.ViewHolder>{

    Context context;
    Utilities utilities;
    String SERVER_URL = "";
    View layout;
    InputMethodManager imm;

    int[] images = new int[]{R.drawable.a_services,R.drawable.a_booking,R.drawable.a_queuing,R.drawable.a_location,R.drawable.a_faq,R.drawable.a_giftbox};
    String[] titles = new String[] {"Services","Book Now","Queuing","Branches", "FAQ's","E-gift"};

    public RecyclerButton(Context activity) {
        this.context            = activity;
        this.utilities          = new Utilities(activity);
    }

    @Override
    public RecyclerButton.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        layout                  = LayoutInflater.from(context).inflate(R.layout.recycler_navigation_button,parent,false);
        SERVER_URL              = utilities.returnIpAddress();
        ViewHolder vh           = new RecyclerButton.ViewHolder(layout);
        imm                     = (InputMethodManager)context.getSystemService(Service.INPUT_METHOD_SERVICE);
        return vh;

    }

    @Override
    public void onBindViewHolder(final RecyclerButton.ViewHolder holder, final int position) {
        final ViewHolder view = holder;
        String title          =  titles[position];
        view.lblTitle.setText(title);
        view.grid_image.setImageDrawable(context.getResources().getDrawable(images[position]));
        holder.cardview_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specifyFunction(position);
            }
        });
    }

    private void specifyFunction(int position) {

        if(position == 0){
            //Services
            Intent intent = new Intent(context,ServicePackageProduct.class);
            context.startActivity(intent);
        }
        else if(position == 1){
            String clientID = utilities.getClientID();
            if (clientID != null){
                AppointmentSingleton appointmentSingleton = new AppointmentSingleton();
                appointmentSingleton.Instance().setAppReserved(utilities.getCurrentDate());
                Intent intent = new Intent(context,AppointmentForm.class);
                context.startActivity(intent);
            }
            else{
                utilities.showDialogMessage("Log-In to continue Booking","Please log-in your account before you continue to book an appointment","info");
            }
        }
        else if(position == 2){
            Intent intent = new Intent(context,Queuing.class);
            context.startActivity(intent);
        }
        else if(position == 3){
            Intent intent = new Intent(context,LocationIndex.class);
            context.startActivity(intent);
        }
        else if(position == 4){
            Intent intent = new Intent(context,FAQ.class);
            context.startActivity(intent);
        }
        else if(position == 5){
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://giftaway.ph/laybare?utm_source=laybare&utm_medium=mobile&utm_campaign=direct")));
        }
    }



    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView lblTitle;
        ImageView grid_image;
        CardView cardview_button;

        public ViewHolder(final View itemView) {
            super(itemView);
            lblTitle         = (TextView)itemView.findViewById(R.id.grid_text);
            grid_image       = (ImageView)itemView.findViewById(R.id.grid_image);
            cardview_button  = (CardView) itemView.findViewById(R.id.cardview_button);
        }


    }
}