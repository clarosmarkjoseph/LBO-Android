package com.system.mobile.lay_bare.Transactions;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Mark on 18/10/2017.
 */

public class BookingInfoRecycler extends RecyclerView.Adapter<BookingInfoRecycler.ViewHolder> {
    Context context;
    View view;
    Utilities utilities;
    String SERVER_URL = "";

    String origStartTime,copyStartTime;


    AppointmentSingleton appointmentSingleton;
    boolean isLoaded  = false;
    int countService  = 0;
    int totalQuantity = 0;
    double totalPrice = 0;

    JSONArray arrayItem;
    TextView lblTotalQuantity,lblEmpty;
    TextView lblTotalPrice;
    RecyclerView recyclerItem;


    public BookingInfoRecycler(Context ctx, String Start_time, TextView lblEmpty, TextView lblTotalQuantity, TextView lblTotalPrice, RecyclerView recyclerItem){
        this.context                = ctx;
        this.utilities              = new Utilities(context);
        this.origStartTime          = Start_time;
        this.copyStartTime          = Start_time;
        this.lblTotalQuantity       = lblTotalQuantity;
        this.lblTotalPrice          = lblTotalPrice;
        this.appointmentSingleton   = new AppointmentSingleton();
        this.arrayItem              = appointmentSingleton.Instance().getArrayItems();
        this.recyclerItem           = recyclerItem;
        this.lblEmpty               = lblEmpty;

    }

    @Override
    public BookingInfoRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SERVER_URL  = utilities.returnIpAddress();
        isLoaded    = true;
        view        = LayoutInflater.from(context).inflate(R.layout.recycler_booking_info,parent,false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        calculateTime(position);
        final ViewHolder view   = holder;
        final JSONObject jsonObject;
        try {
            jsonObject                      = arrayItem.getJSONObject(position);
            int itemID                      = jsonObject.getInt("item_id");
            String itemType                 = jsonObject.getString("item_type");
            String itemName                 = jsonObject.getString("item_name");
            final Double itemPrice                = jsonObject.optDouble("item_price",0);
            int itemDuration                = 0;
            String itemImage                = "";
            String start_time               = "";
            String end_time                 = "";

            if(itemType.equals("Services") || itemType.equals("Packages")){

                view.linear_time.setVisibility(View.VISIBLE);
                view.linear_qty.setVisibility(View.GONE);

                itemImage               = SERVER_URL + "/images/services/"+jsonObject.getString("item_image").replace(" ","%20");
                itemDuration            = jsonObject.optInt("item_duration",0);
                start_time              = jsonObject.getString("item_start_time");
                end_time                = jsonObject.getString("item_end_time");

                String[] arraySpinner = new String[] {
                        "x1"
                };

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                view.spinnerQuantity.setAdapter(adapter);

                view.lblStart.setText(utilities.convert12Hours(start_time));
                view.lblEnd.setText(utilities.convert12Hours(end_time));

                view.lblInitPrice.setText(String.valueOf(itemPrice));
                view.lblPrice.setText(String.valueOf(itemPrice));
                view.lblDesc2.setText(String.valueOf(itemDuration)+ " minutes");
                utilities.setUniversalBigImage(view.imgItems,itemImage);


            }
            else{

                view.linear_time.setVisibility(View.GONE);
                view.linear_qty.setVisibility(View.VISIBLE);

                String itemVariant  = jsonObject.getString("item_variant");
                String itemSize     = jsonObject.getString("item_size");
                itemImage           = SERVER_URL + "/images/products/"+jsonObject.getString("item_image").replace(" ","%20");
                int itemQty         = jsonObject.optInt("item_quantity",1);

                int spinnerPos = itemQty-=1;
                String[] arraySpinner = new String[] {
                        "x1", "x2", "x3", "x4", "x5"
                };

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                view.spinnerQuantity.setAdapter(adapter);
                view.spinnerQuantity.setSelection(spinnerPos);

                view.lblInitPrice.setText("₱ "+utilities.convertToCurrency(String.valueOf(itemPrice)));
                view.lblPrice.setText("₱ "+utilities.convertToCurrency(String.valueOf(itemPrice)));
                view.lblDesc2.setText(itemVariant+"\n"+itemSize);

                view.spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        int total_qty = pos+=1;
                        if (total_qty > 5){
                            utilities.showDialogMessage("Too many item(s)","Sorry, you can only book a maximum of 5 items","error");
                        }
                        else{
                            double total_price  = itemPrice * total_qty;
                            try {
                                jsonObject.put("item_quantity",total_qty);
                                holder.lblPrice.setText("Php "+ utilities.convertToCurrency(String.valueOf(total_price)));
                                computeTotalItemsAndPrice();
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
            view.lblItemName.setText(itemName);
            view.lblPrice.setText("Php "+utilities.convertToCurrency(String.valueOf(itemPrice)));
            utilities.setUniversalBigImage(holder.imgItems,itemImage);

            if(position == arrayItem.length() - 1){
                computeTotalItemsAndPrice();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
                arrayItem.remove(position);
                appointmentSingleton.Instance().setArrayItems(arrayItem);
                copyStartTime = origStartTime;

                if(arrayItem.length() <= 0){
                    recyclerItem.setVisibility(View.GONE);
                    lblEmpty.setVisibility(View.VISIBLE);
                    lblTotalQuantity.setText("0");
                    lblTotalPrice.setText("₱ 0.00");
                }

            }
        });

    }

    private void computeTotalItemsAndPrice() {

        totalQuantity           = 0;
        totalPrice              = 0;

        if (arrayItem.length() > 0) {
            for (int x = 0; x < arrayItem.length(); x++) {
                try {
                    JSONObject jsonObject       = arrayItem.getJSONObject(x);
                    String type                 = jsonObject.optString("item_type", "");
                    int qtyItem                 = jsonObject.getInt("item_quantity");

                    if (type.equals("Services") || type.equals("Packages")) {
                        double subTotal = jsonObject.optDouble("item_price", 0);
                        totalQuantity       += 1;
                        totalPrice          += subTotal;
                    }
                    else {
                        double prPrice      = jsonObject.optDouble("item_price", 0);
                        double subTotal     = prPrice * qtyItem;
                        totalQuantity       += qtyItem;
                        totalPrice          += subTotal;
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            lblTotalQuantity.setText(String.valueOf(totalQuantity));
            lblTotalPrice.setText("₱ " + utilities.convertToCurrency(String.valueOf(totalPrice)));
        }
        else {
            lblEmpty.setVisibility(View.VISIBLE);
            lblTotalQuantity.setText("0");
            lblTotalPrice.setText("₱ 0.00");
        }
    }



    public void calculateTime(int position){

        String initStart        = copyStartTime;
        String end_time         = "";

        if(position == 0){
            copyStartTime = origStartTime;
            initStart     = copyStartTime;
        }

        try {
            JSONObject jsonObject   = arrayItem.getJSONObject(position);
            String type             = jsonObject.getString("item_type");
            if(type.equals("Services") || type.equals("Packages")){
                int duration = Integer.parseInt(jsonObject.getString("item_duration"));

                if (countService == 0 && (type.equals("Services") || type.equals("Packages"))){
                    initStart  = copyStartTime;
                    end_time   = utilities.getEndTimeByDuration(initStart,duration);
                    Log.e("postion",String.valueOf(position)+"||"+initStart+" - "+end_time);
                }
                else{
                    end_time   = utilities.getEndTimeByDuration(initStart,duration);
                }
                jsonObject.put("item_start_time",initStart);
                jsonObject.put("item_end_time",end_time);
                arrayItem.put(position,jsonObject);
                copyStartTime  = end_time;
                countService++;
            }
            else{
                jsonObject.put("item_start_time","");
                jsonObject.put("item_end_time","");
                arrayItem.put(position,jsonObject);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }






    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout linear_time,linear_qty;
        public ImageView imgItems;
        public TextView lblItemName;
        public TextView lblDesc2;
        public TextView lblPrice;
        public TextView lblStart;
        public TextView lblEnd;
        public TextView lblInitPrice;
        public ImageButton btnRemove;
        Spinner spinnerQuantity;

        public ViewHolder(View itemView) {
            super(itemView);

            linear_time     = (LinearLayout)itemView.findViewById(R.id.linear_time);
            linear_qty      = (LinearLayout)itemView.findViewById(R.id.linear_qty);
            imgItems        = (ImageView)itemView.findViewById(R.id.imgItems);
            lblItemName     = (TextView)itemView.findViewById(R.id.lblItemName);
            lblDesc2        = (TextView)itemView.findViewById(R.id.lblDesc2);
            lblPrice        = (TextView)itemView.findViewById(R.id.lblPrice);
            btnRemove       = (ImageButton)itemView.findViewById(R.id.btnRemove);

            lblEnd          = (TextView)itemView.findViewById(R.id.lblEnd);
            lblStart        = (TextView)itemView.findViewById(R.id.lblStart);
            lblInitPrice    = (TextView)itemView.findViewById(R.id.lblInitPrice);
            spinnerQuantity = (Spinner)itemView.findViewById(R.id.spinnerQuantity);

        }
    }




    @Override
    public int getItemCount() {
        return arrayItem.length();
    }

}

