package com.system.mobile.lay_bare.Profile;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.system.mobile.lay_bare.R;

/**
 * Created by Mark on 05/09/2017.
 */

public class PromotionDetails extends AppCompatActivity {

    ImageView imgPromotionDetails;
    TextView lblTitleDetails,lblDateDetails,lblContentDetails;
    String extraTitle,extraDate,extraImage,extraContent;
    RelativeLayout relativePromotionDetails;
    Toolbar toolbarPromotionDetails;
    TextView navTitle;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotions);
    }

    private void loadPromotionDetails(final String extraImage) {
        Picasso.with(getApplicationContext())
                .load(extraImage)
                .placeholder(R.drawable.no_image)
//                .resize(300,300)
//                .centerCrop()

                .noFade()
                .error(R.drawable.no_image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imgPromotionDetails, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                            //Try again online if cache failed
                        Picasso.with(getApplicationContext())
                                .load(extraImage)
//                                .resize(300,300)
//                                .centerCrop()
                                .noFade()
                                .error(R.drawable.no_image)
                                .into(imgPromotionDetails, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }
                                    @Override
                                    public void onError() {
                                        Snackbar bar = Snackbar.make(relativePromotionDetails,"Server Connection Lost", Snackbar.LENGTH_LONG);
                                        bar.show();
                                    }
                                });
                    }
                });
//        imgPromotionDetails.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imgPromotionDetails.getLayoutParams().width = 250;
//        imgPromotionDetails.getLayoutParams().height = 250;
//        imgPromotionDetails.setAdjustViewBounds(true);
    }

    //to back pressed in navigation bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);

    }



}
