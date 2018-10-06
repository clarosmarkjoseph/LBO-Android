package com.system.mobile.lay_bare.Location;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.system.mobile.lay_bare.MySingleton;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by paolohilario on 1/9/18.
 */

public class LocationReview extends Fragment {

    View layout;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerLayoutManager;
    RecyclerView.Adapter recyclerViewAdapter;
    HorizontalBarChart barChart;
    ArrayList<BarEntry> bargroup;
    RatingBar ratingBar;
    BarDataSet set1;
    LinearLayout linearContent,linear_loading;
    LocationClassSingleton locationClassSingleton;
    Utilities utilities;
    int offset = 0;
    TextView lblBranchAverageReview,lblRateCount,lblEmpty;
    ViewPager viewPager;
    boolean isLoading = false;
    boolean ifVisible = false;
    String SERVER_URL = "";
    int branch_id = 0;
    JSONArray arrayRatings;
    JSONArray arrayReviews;

    private ArrayList<String> arrayErrorResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.location_review,container,false);
        return layout;
    }

    private void initElement() {

        barChart                    = (HorizontalBarChart) layout.findViewById(R.id.chart);
        ratingBar                   = (RatingBar)layout.findViewById(R.id.ratingBar);
        linearContent               = (LinearLayout)layout.findViewById(R.id.linearContent);
        linear_loading              = (LinearLayout)layout.findViewById(R.id.linear_loading);
        lblBranchAverageReview      = (TextView)layout.findViewById(R.id.lblBranchAverageReview);
        lblRateCount                = (TextView)layout.findViewById(R.id.lblRateCount);
        lblEmpty                    = (TextView)layout.findViewById(R.id.lblEmpty);
        recyclerView                = (RecyclerView)layout.findViewById(R.id.recyclerReviews);
        locationClassSingleton      = new LocationClassSingleton();
        utilities                   = new Utilities(getActivity());

        arrayReviews                = locationClassSingleton.Instance().getArrayReviews();
        arrayRatings                = locationClassSingleton.Instance().getArrayListRatings();
        viewPager                   = locationClassSingleton.Instance().getViewPager();

        SERVER_URL                  = utilities.returnIpAddress();
        offset                      = arrayRatings.length();

        getReviewDetails();

        if(arrayRatings.length() > 0){
            displayRecyclerView();
        }
        else{
            recyclerView.setVisibility(View.GONE);
            lblEmpty.setVisibility(View.VISIBLE);
        }

        viewPager.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) viewPager.getChildAt(viewPager.getChildCount() - 1);
                int diff = (view.getBottom() - (viewPager.getHeight() + viewPager.getScrollY()));
                if (diff == 0) {
                    if(isLoading == false && arrayReviews.length() > 20){
                        getMoreReviews();
                    }
                }
            }
        });
    }

    private void getMoreReviews() {
        isLoading = true;
        showLoading();
        String url = SERVER_URL+"/api/mobile/getBranchRatings";
        Log.e("URL:",url);
        StringRequest arrayRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject objectResult = null;
                        try {
                            objectResult = new JSONObject(response);
                            JSONArray resReview   = objectResult.getJSONArray("arrayReview");
                            offset                   = objectResult.getInt("offset");
                            if(resReview.length() > 0){
                                for(int x = 0; x < resReview.length(); x++){
                                    JSONObject objectResultReview = resReview.getJSONObject(x);
                                    arrayReviews.put(objectResultReview);
                                }
                                locationClassSingleton.Instance().setOffset(offset);
                                locationClassSingleton.Instance().setArrayReviews(arrayReviews);
                                recyclerViewAdapter.notifyDataSetChanged();
                            }

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isLoading = false;
                                    hideLoading();
                                }
                            }, 300);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        arrayErrorResponse = utilities.errorHandling(error);
                        Toast.makeText(getActivity(),arrayErrorResponse.get(1).toString(),Toast.LENGTH_LONG).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isLoading = false;
                                hideLoading();
                            }
                        }, 300);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("branch_id", String.valueOf(branch_id));
                params.put("offset",String.valueOf(offset));
                params.put("getAllDetails",String.valueOf(false));
                Log.e("PARAMS",params.toString());
                return params;
            }
        };
        arrayRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        20000,
                        2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        MySingleton.getInstance(getActivity()).addToRequestQueue(arrayRequest);
    }

    private void displayRecyclerView(){
        if (arrayReviews.length() > 0){
            recyclerView.setVisibility(View.VISIBLE);
            lblEmpty.setVisibility(View.GONE);
        }
        else{
            recyclerView.setVisibility(View.GONE);
            lblEmpty.setVisibility(View.VISIBLE);
        }
        recyclerViewAdapter     = new RecyclerBranchReview(getActivity(),arrayReviews);
        recyclerLayoutManager   = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void getReviewDetails() {

        showLoading();
        set1            = new BarDataSet(getDataSet(), "");
        set1.setColors(getActivity().getResources().getColor(R.color.brownLoading), getActivity().getResources().getColor(R.color.brownLoading), getActivity().getResources().getColor(R.color.brownLoading), getActivity().getResources().getColor(R.color.brownLoading), getActivity().getResources().getColor(R.color.brownLoading));
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);
        // hide Y-axis
        YAxis left = barChart.getAxisLeft();
        left.setDrawLabels(false);
        // custom X-axis labels
        String[] values = new String[] { "1★", "2★", "3★", "4★", "5★"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        left.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        // custom description
        Description description = new Description();
        description.setText("Branch & Technician Ratings");
        barChart.setDescription(description);
        // hide legend
        barChart.getLegend().setEnabled(false);
        barChart.getLegend().setDrawInside(false);

        //hide grid
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.animateY(1000);
        barChart.setEnabled(false);
        barChart.setMaxVisibleValueCount(5);
        barChart.setVisibleXRangeMaximum(5); // allow 6 values to be displayed at once on the x-axis, not more
        barChart.moveViewToX(1);
        barChart.setDragEnabled(false);
        barChart.setData(data);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();



        double totalReviews =  locationClassSingleton.Instance().getTotalReview();
        double totalRatings =  locationClassSingleton.Instance().getTotalRatings();
//        double ratingVal = utilities.getAverageTotalReviews(arrayReview);
        lblRateCount.setText(String.valueOf(totalReviews));
        if (totalRatings > 0){
            lblBranchAverageReview.setText(String.valueOf(utilities.roundOffDecimal(totalRatings)));
            ratingBar.setRating((float) totalRatings);
        }
        else{
            lblBranchAverageReview.setText("0");
            ratingBar.setRating(0f);
        }


    }

    private ArrayList<BarEntry> getDataSet() {

        bargroup = new ArrayList<>();
        BarEntry v1 = null;
        BarEntry v2 = null;
        BarEntry v3 = null;
        BarEntry v4 = null;
        BarEntry v5 = null;

        if(arrayRatings.length() > 0){
            ArrayList<Float>  arrayValue = utilities.getArrayOfAverageForEachRating(arrayRatings);
            v1 = new BarEntry(0f, arrayValue.get(0));
            v2 = new BarEntry(1f, arrayValue.get(1));
            v3 = new BarEntry(2f, arrayValue.get(2));
            v4 = new BarEntry(3f, arrayValue.get(3));
            v5 = new BarEntry(4f, arrayValue.get(4));
        }
        else{
            v1 = new BarEntry(0f, 0);
            v2 = new BarEntry(1f, 0);
            v3 = new BarEntry(2f, 0);
            v4 = new BarEntry(3f, 0);
            v5 = new BarEntry(4f, 0);
        }

        bargroup.add(v1);
        bargroup.add(v2);
        bargroup.add(v3);
        bargroup.add(v4);
        bargroup.add(v5);

        hideLoading();
        return bargroup;

    }

    public void showLoading(){
        linear_loading.setVisibility(View.VISIBLE);
    }

    private void showNoInternet(){
        linear_loading.setVisibility(View.GONE);
    }
    private void hideLoading(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                linear_loading.setVisibility(View.GONE);
            }
        }, 3000);

    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }

    }


    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible & ifVisible == false) {
            ifVisible = true;
            Log.e("HAIST VISIBLE","VISIBLE");
            initElement();
        }
    }


}
