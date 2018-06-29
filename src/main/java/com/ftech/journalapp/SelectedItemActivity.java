package com.ftech.journalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class SelectedItemActivity extends AppCompatActivity
        implements View.OnClickListener{
    //----------
    TextView mDesc,mInterval,mStartDate,mEndDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_from_right,R.anim.fadeout_a_bit);
        setContentView(R.layout.activity_selected_item);
        //set the toolbar with back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //initialize the textViews from layout
        mDesc = (TextView)findViewById(R.id.medDescLabel);
        mInterval = (TextView)findViewById(R.id.medIntervalLabel);
        mStartDate = (TextView)findViewById(R.id.startDateLabel);
        mEndDate = (TextView)findViewById(R.id.endDateLabel);
        //Run some loads in another  thread
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //use the medication name as the title of the page
                setTitle(getIntent().getStringExtra("name"));
                //set description
                mDesc.setText(getIntent().getStringExtra("note"));
                //get the name which was used to save the intervals per day
                SharedPreferences mPref = getSharedPreferences(getIntent().getStringExtra("name"),MODE_PRIVATE);
                //SharedPreferences.Editor mEditor = sPref.edit();
                mInterval.setText(mPref.getString("morning","")+mPref.getString("afternoon","")+mPref.getString("evening",""));
                // Split the String start and end date by dash delimiter and generate an string array
                String[] dateArray = getIntent().getStringExtra("medDate").split("-");
                // Save the start and end date
                mStartDate.setText(dateArray[0]);
                mEndDate.setText(dateArray[1]);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein_a_bit,R.anim.slide_out_from_right);
    }

    @Override
    public void onClick(View view) {

    }
}
