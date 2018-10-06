
package com.system.mobile.lay_bare.PLC;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.system.mobile.lay_bare.ClientTransactions.ClientTransactions;
import com.system.mobile.lay_bare.ClientTransactions.RecyclerTransactionLog;
import com.system.mobile.lay_bare.DataHandler;
import com.system.mobile.lay_bare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by paolohilario on 9/18/18.
 */

public class FragmentTransactionReview extends Fragment {

    View layout;
    RecyclerView recyclerPLCApplication;
    RecyclerView.LayoutManager  recyclerLayout;
    RecyclerView.Adapter recyclerAdapter;
    DataHandler handler;
    ArrayList<JSONObject> arrayList;
    TextView lblText;
    TextView lblTitle;

    public static FragmentApplication getInstance(int position) {
        FragmentApplication fragmentService = new FragmentApplication();
        Bundle args = new Bundle();
        args.putInt("position",position);
        fragmentService.setArguments(args);
        return fragmentService;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout      = inflater.inflate(R.layout.fragment_plc_application, container, false);
        handler     = new DataHandler(getActivity());
        lblText     = (TextView)layout.findViewById(R.id.lblText);
        initElements();
        return layout;
    }

    private void initElements() {

        arrayList               = new ArrayList<>();
        lblTitle                = (TextView)layout.findViewById(R.id.lblTitle);
        recyclerPLCApplication  = (RecyclerView)layout.findViewById(R.id.recyclerPLCApplication);
        lblTitle.setText("List of Transaction Request");
        handler.open();
        Cursor c = handler.returnPLCApplicationAndReviewLogs();
        if(c.getCount() > 0){
            c.moveToFirst();
            try {
                JSONArray arrayPLCApplication =  new JSONArray(c.getString(1));
                for(int x = 0; x < arrayPLCApplication.length(); x++){
                    JSONObject objectApplication = arrayPLCApplication.getJSONObject(x);
                    Log.e("WEE",String.valueOf(objectApplication));
                    arrayList.add(objectApplication);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        handler.close();
        recyclerAdapter         = new RecyclerPLCLogs(getActivity(),arrayList,false);
        recyclerLayout          = new LinearLayoutManager(getActivity());
        recyclerPLCApplication.setAdapter(recyclerAdapter);
        recyclerPLCApplication.setFocusableInTouchMode(false);
        recyclerPLCApplication.setNestedScrollingEnabled(false);
        recyclerPLCApplication.setHasFixedSize(false);
        recyclerPLCApplication.setLayoutManager(recyclerLayout);
        recyclerPLCApplication.setItemAnimator(new DefaultItemAnimator());
        if(arrayList.size() > 0){
            lblText.setVisibility(View.GONE);
            recyclerPLCApplication.setVisibility(View.VISIBLE);
        }
        else{
            recyclerPLCApplication.setVisibility(View.GONE);
            lblText.setVisibility(View.VISIBLE);
        }
    }
}
