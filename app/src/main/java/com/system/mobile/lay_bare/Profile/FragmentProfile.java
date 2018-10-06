package com.system.mobile.lay_bare.Profile;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.system.mobile.lay_bare.Classes.ProfileClass;
import com.system.mobile.lay_bare.R;
import com.system.mobile.lay_bare.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by paolohilario on 1/15/18.
 */

public class FragmentProfile extends Fragment {

    View layout;
    TextView lblHomeBranch,lblName,lblAddress,lblContact,lblGender,lblBirthday,lblEmail;
    ImageButton imgbtn_edit_account,imgbtn_edit_personal,imgbtn_edit_branch;
    String clientID,SERVER_URL;
    Utilities utilities;
    boolean ifPaused = false;

    public static FragmentProfile getInstance(int position) {
        FragmentProfile fragmentProfile = new FragmentProfile();
        Bundle args = new Bundle();
        args.putInt("position",position);
        fragmentProfile.setArguments(args);
        return fragmentProfile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       layout = inflater.inflate(R.layout.fragment_profile,container,false);
       initElement();
       return layout;
    }

    private void initElement() {

        utilities            = new Utilities(getActivity());
        SERVER_URL           = utilities.returnIpAddress();
        lblName              = (TextView)layout.findViewById(R.id.lblName);
        lblHomeBranch        = (TextView)layout.findViewById(R.id.lblHomeBranch);
        lblAddress           = (TextView)layout.findViewById(R.id.lblAddress);
        lblContact           = (TextView)layout.findViewById(R.id.lblContact);
        lblGender            = (TextView)layout.findViewById(R.id.lblGender);
        lblBirthday          = (TextView)layout.findViewById(R.id.lblBirthday);
        lblEmail             = (TextView)layout.findViewById(R.id.lblEmail);
        imgbtn_edit_account  = (ImageButton)layout.findViewById(R.id.imgbtn_edit_account);
        imgbtn_edit_personal = (ImageButton)layout.findViewById(R.id.imgbtn_edit_personal);
        imgbtn_edit_branch   = (ImageButton)layout.findViewById(R.id.imgbtn_edit_branch);

        imgbtn_edit_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ClientEditProfile.class);
                intent.putExtra("Options","Account");
                startActivity(intent);
            }
        });

        imgbtn_edit_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ClientEditProfile.class);
                intent.putExtra("Options","Personal");
                startActivity(intent);
            }
        });

        imgbtn_edit_branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ClientEditProfile.class);
                intent.putExtra("Options","Branch");
                startActivity(intent);
            }
        });
        loadClientProfile();
    }

    private void loadClientProfile() {

        ProfileClass profileClass = new ProfileClass(getActivity());
        try {
            JSONObject objectResponse   = profileClass.returnClientObject();
            clientID                    = objectResponse.getString("id");
            String email                = utilities.changeNullString(objectResponse.getString("email"));
            String first_name           = utilities.changeNullString(objectResponse.getString("first_name"));
            String middle_name          = utilities.changeNullString(objectResponse.getString("middle_name"));
            String last_name            = utilities.changeNullString(objectResponse.getString("last_name"));
            String birth_date           = utilities.changeNullString(objectResponse.getString("birth_date"));
            String user_mobile          = utilities.changeNullString(objectResponse.getString("user_mobile"));
            String gender               = utilities.changeNullString(objectResponse.getString("gender"));
            String user_address         = utilities.changeNullString(objectResponse.getString("user_address"));
            JSONObject objClient        = new JSONObject(objectResponse.getString("user_data"));
            String branch_id            = utilities.changeNullString(objClient.getString("home_branch"));

            lblName.setText(first_name+" "+middle_name+" "+last_name);
            lblAddress.setText(user_address);
            lblContact.setText(user_mobile);
            lblGender.setText(utilities.capitalize(gender));
            lblBirthday.setText(utilities.getBirthdayAsWord(birth_date));
            lblEmail.setText(email);
            lblHomeBranch.setText(profileClass.getHomeBranch(branch_id));
            utilities.hideProgressDialog();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ifPaused == true){
            ifPaused = false;
            loadClientProfile();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ifPaused = true;
    }


}
