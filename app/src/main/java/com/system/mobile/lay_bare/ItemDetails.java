package com.system.mobile.lay_bare;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.system.mobile.lay_bare.Utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Mark on 06/10/2017.
 */


public class ItemDetails extends AppCompatActivity {

    ImageView imgItem;

    Utilities utilities;
    String SERVER_URL = "";
    DataHandler handler;
    TextView lblName,lblDescription,lblPrice,lblTitle1,lblTitle2,lblTitle3,lblAnswer1,lblAnswer2,lblAnswer3;
    String item_type    = "";
    JSONObject objectItem;
    LinearLayout linear_product_size;
    private Typeface myTypeface;
    private ImageButton imgBtnBack;
    private TextView forTitle;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        handler             = new DataHandler(getApplicationContext());
        utilities           = new Utilities(this);
        imgItem             = (ImageView)findViewById(R.id.imgItem);
        SERVER_URL          = utilities.returnIpAddress();
        linear_product_size = (LinearLayout)findViewById(R.id.linear_product_size);
        lblName             = (TextView)findViewById(R.id.lblName);
        lblDescription      = (TextView)findViewById(R.id.lblDescription);
        lblPrice            = (TextView)findViewById(R.id.lblPrice);
        lblTitle1           = (TextView)findViewById(R.id.lblTitle1);
        lblTitle2           = (TextView)findViewById(R.id.lblTitle2);
        lblTitle3           = (TextView)findViewById(R.id.lblTitle3);
        lblAnswer1          = (TextView)findViewById(R.id.lblAnswer1);
        lblAnswer2          = (TextView)findViewById(R.id.lblAnswer2);
        lblAnswer3          = (TextView)findViewById(R.id.lblAnswer3);
        setFonts();
        getExtra();
    }

    private void getExtra() {
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            item_type       = bundle.getString("item_type");
            try {
                objectItem      = new JSONObject(bundle.getString("objectItem"));

                if(item_type.equals("Services")){
                    String item_id              = objectItem.getString("id");
                    String service_name         = objectItem.getString("service_name");
                    String service_description  = objectItem.getString("service_description");
                    String service_gender       = objectItem.getString("service_gender");
                    String service_picture      = objectItem.getString("service_picture");
                    String service_minutes      = objectItem.getString("service_minutes");
                    String service_price        = objectItem.getString("service_price");

                    lblName.setText(service_name);
                    lblDescription.setText(service_description);
                    lblTitle1.setText("Service Gender: ");
                    lblAnswer1.setText(utilities.capitalize(service_gender));
                    lblTitle2.setText("Service Duration: ");
                    lblAnswer2.setText(service_minutes +" minutes");
                    lblPrice.setText("Php "+utilities.convertToCurrency(service_price));
                    handler.close();
                    String imgUrl = SERVER_URL+"/images/services/"+service_picture;
                    utilities.setUniversalBigImage(imgItem,imgUrl);
                    forTitle.setText("Services");

                }
                else if(item_type.equals("Packages")){

                    String item_id              = objectItem.getString("id");
                    String service_name         = objectItem.getString("package_name");
                    String service_description  = objectItem.getString("package_desc");
                    String service_picture      = objectItem.getString("package_image");
                    String service_minutes      = objectItem.getString("package_duration");
                    String service_price        = objectItem.getString("package_price");
                    lblName.setText(service_name);
                    lblDescription.setText(service_description);
                    lblTitle1.setText("Item Type: ");
                    lblAnswer1.setText("Service Package: ");
                    lblTitle2.setText("Package Duration: ");
                    lblAnswer2.setText(service_minutes +" minutes");
                    lblPrice.setText("Php "+utilities.convertToCurrency(service_price));
                    handler.close();
                    String imgUrl = SERVER_URL+"/images/services/"+service_picture;
                    utilities.setUniversalBigImage(imgItem,imgUrl);
                    forTitle.setText("Cool Packages");

                }
                else{
                    linear_product_size.setVisibility(View.VISIBLE);
                    lblTitle1.setText("");
                    lblTitle2.setText("");

                    String item_id              = objectItem.getString("id");
                    String product_name         = objectItem.getString("product_group_name");
                    String product_description  = objectItem.getString("product_description");
                    String product_variant      = objectItem.getString("product_variant");
                    String product_size         = objectItem.getString("product_size");
                    String product_picture      = objectItem.getString("product_picture");
                    String product_price        = objectItem.getString("product_price");
                    lblName.setText(product_name);
                    lblDescription.setText(product_description);
                    lblTitle1.setText("Product CODE: ");
                    lblTitle2.setText("Product Variant: ");
                    lblTitle3.setText("Product Size");
                    lblAnswer1.setText("");
                    lblAnswer2.setText(product_variant);
                    lblAnswer3.setText(product_size);
                    lblPrice.setText("Php "+utilities.convertToCurrency(product_price));
                    handler.close();
                    String imgUrl = SERVER_URL+"/images/products/"+product_picture;
                    utilities.setUniversalBigImage(imgItem,imgUrl);
                    forTitle.setText("Products");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public void setFonts(){
        myTypeface          = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        forTitle                = (TextView)findViewById(R.id.forTitle);
        imgBtnBack              = (ImageButton) findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        forTitle.setTypeface(myTypeface);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
