package com.system.mobile.lay_bare.ClientTransactions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.system.mobile.lay_bare.Location.RecyclerBranchDetails;
import com.system.mobile.lay_bare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Mark on 19/12/2017.
 */

public class RecyclerTransactionDetails extends RecyclerView.Adapter<RecyclerTransactionDetails.ViewHolder>  {

    Context context;
    JSONArray arrayServices;
    View view;

    public RecyclerTransactionDetails(Context ctx, JSONArray array) {
        this.context        = ctx;
        this.arrayServices  = array;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(context).inflate(R.layout.client_transaction_details_recycler, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

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
    public void onBindViewHolder(ViewHolder holder, int position) {

        try{
            JSONObject object = arrayServices.getJSONObject(position);
            String item_type  = object.getString("item_unit");
            String item_name  = object.getString("item_name");
            String quantity  = object.getString("quantity");
            String sub_total  = object.getString("sub_total");

            holder.lblType.setText(item_type);
            holder.lblService.setText(item_name);
            holder.lblPrice.setText("Php "+sub_total);
            if(item_type.equals("Services")){
                holder.imgType.setImageDrawable(context.getResources().getDrawable(R.drawable.a_services));
                holder.lblQuantity.setText(quantity+" service");
            }
            else{
                holder.imgType.setImageDrawable(context.getResources().getDrawable(R.drawable.a_products));
                holder.lblQuantity.setText(quantity+" pc(s)/qty");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayServices.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgType;
        TextView lblType,lblService,lblQuantity,lblPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            imgType     = (ImageView)itemView.findViewById(R.id.imgType);
            lblPrice    = (TextView)itemView.findViewById(R.id.lblPrice);
            lblService  = (TextView)itemView.findViewById(R.id.lblService);
            lblQuantity = (TextView)itemView.findViewById(R.id.lblQuantity);
            lblType     = (TextView)itemView.findViewById(R.id.lblType);
        }
    }


}
