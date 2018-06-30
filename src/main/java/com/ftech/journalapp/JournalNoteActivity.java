package com.ftech.journalapp;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ftech.journalapp.utils.Constants;
import com.ftech.journalapp.mDataBase.DBAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * This class is used to put down notes, saves them in a database and at the same time can still be used to update/edit an existing note in the database
 */
public class JournalNoteActivity extends AppCompatActivity implements View.OnClickListener{

    //title and its note AppCompatTextView
    AppCompatEditText mTitle, mNote;
    //the add button
    AppCompatTextView mAddBtn;
    TextView mNoteLabel;
    AppCompatTextView mPlainNote,mPlainName;
    private boolean saved;
    private SharedPreferences mPref;
    private int mItemId;
    private String mColor;
    //individual item's icon mColor
    String[] mColorArray = {"#9c0a90","#f64c74","#20d2cc","#4495ff","#6145a3","#d11515",
            "#d1395c","#FF081453","#FF530841","#FF530812","#FF0B4E42","#FF027594"};//d1395c
    //--------
    //layout for direction on how to enter edit mode
    private RelativeLayout mInfoTextBg;
    //check the height. It will be used for animating the view
    private int mInfoTextHeight;
    //declare variables that will help check when the title and the note are edited by storing their initial values,
    //which will be used to compare them when the Update button is pressed
    private StringBuilder mOldTitle,mOldNote;
    private boolean isTitleEdited,isNoteEdited;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_from_right,R.anim.fadeout_a_bit);
        setContentView(R.layout.activity_add_journal_item);
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
        mOldTitle = new StringBuilder();
        mOldNote = new StringBuilder();
        //--------Initialize the name and description of the journal
        mTitle = (AppCompatEditText)findViewById(R.id.name);
        mNote = (AppCompatEditText)findViewById(R.id.note);
        mNote.setSingleLine(false);
        //
        mNoteLabel = (TextView) findViewById(R.id.noteLabel);
        //----------------
        mPlainName = (AppCompatTextView) findViewById(R.id.plainName);
        mPlainNote = (AppCompatTextView) findViewById(R.id.plainNote);
        //----------------
        mAddBtn = (AppCompatTextView) findViewById(R.id.addBtn);
        //-----------
        mInfoTextBg = (RelativeLayout) findViewById(R.id.infoTextBg);
        mInfoTextBg.measure(0,0);
        mInfoTextHeight = mInfoTextBg.getMeasuredHeight();
        //----------------
        //Asigning onClick listener to views
        mAddBtn.setOnClickListener(this);
        mNoteLabel.setOnClickListener(this);
        mPlainName.setOnClickListener(this);
        mPlainNote.setOnClickListener(this);
        //retrieving data from my preference
        mPref = getSharedPreferences("JOURNAL_NOTE_ADDED",MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        if(mPref.getBoolean("edit_mode",false)){
            String title = getIntent().getStringExtra(Constants.TITLE);
            String desc = getIntent().getStringExtra(Constants.DESC);
            mColor = getIntent().getStringExtra(Constants.COLOR);
            mItemId = getIntent().getIntExtra(Constants.ID,-1);
            setTitle("Update");
            mOldTitle.append(title);
            mOldNote.append(desc);
            mTitle.setVisibility(View.GONE);
            mNote.setVisibility(View.GONE);
            mPlainName.setVisibility(View.VISIBLE);
            mPlainNote.setVisibility(View.VISIBLE);
            mPlainNote.setSingleLine(false);
            mPlainName.setText(title);
            mPlainNote.setText(desc);
            mAddBtn.setText("Update");
            if(!mPref.getBoolean("isFirstTime",false)){
                editor.putBoolean("isFirstTime",true);
                editor.apply();
                showInfoPage();
            }
        }else{
            mAddBtn.setText("Add");
        }
    }

    private void showInfoPage() {
        animateInfoLayout(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateInfoLayout(false);
            }
        },5000);
    }
    public void animateInfoLayout(boolean flag){
        Animation anim1 = AnimationUtils.loadAnimation(this,R.anim.fadein);
        Animation anim2 = AnimationUtils.loadAnimation(this,R.anim.fadeout);
        //----------------
        /*Animation anim3 = AnimationUtils.loadAnimation(this,R.anim.transit_view_down_noalpha);
        Animation anim4 = AnimationUtils.loadAnimation(this,R.anim.transit_view_up_noalpha);*/
        //new animation settings
        ObjectAnimator animInfoUp = ObjectAnimator.ofFloat(mInfoTextBg,"translationY",mInfoTextHeight, 0);
        animInfoUp.setDuration(200);
        animInfoUp.setStartDelay(300);
        ObjectAnimator animInfoDown = ObjectAnimator.ofFloat(mInfoTextBg,"translationY", 0,mInfoTextHeight);
        animInfoDown.setDuration(200);
        if(flag){
            mInfoTextBg.setVisibility(View.VISIBLE);
            mInfoTextBg.startAnimation(anim1);
            animInfoUp.start();
        }else{
            mInfoTextBg.startAnimation(anim2);
            animInfoDown.start();
            anim2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mInfoTextBg.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
    /**
     * This method saves Journal note to database
     * @param name journal note title
     * @param desc journal note
     * @param letter the starting letter of the title.
     */
    private void save(String name,String desc,String letter)
    {
        Date now = new Date();
        DateFormat sdf;
        //format for the date and time when the note was taken
        sdf = new SimpleDateFormat("EEE dd MMM yyyy'. 'hh:mm a ");
        //sdf = new SimpleDateFormat("MMM");
        String mCurrentDateTime = sdf.format(now);
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        //holder.mLetter.setTextColor(Color.parseColor(mColorArray[position]));
        if(mPref.getBoolean("edit_mode",false)){
            saved=db.update(name,desc,letter,mCurrentDateTime, mColor,mItemId);
        }else{
            saved=db.add(name,desc,letter,mCurrentDateTime, mColorArray[new Random().nextInt(mColorArray.length-1)]);
        }
        //Toast.makeText(this, mCurrentMonth, Toast.LENGTH_SHORT).show();
        if(saved)
        {
            if(mPref.getBoolean("edit_mode",false)){
                //my preference
                SharedPreferences.Editor editor = mPref.edit();
                editor.putBoolean("note_added",true);
                editor.remove("edit_mode");
                editor.apply();
                Toast.makeText(JournalNoteActivity.this, "Journal Note Updated!", Toast.LENGTH_SHORT).show();
            }else{
                SharedPreferences.Editor mEditor = mPref.edit();
                mEditor.putBoolean("thereIsDataInDatabase",true);
                mEditor.putBoolean("note_added",true);
                mEditor.apply();
                Toast.makeText(JournalNoteActivity.this, "Journal Note Added!", Toast.LENGTH_SHORT).show();
            }
            onBackPressed();
        }else {
            Toast.makeText(this,"Unable To Save",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * get the sData to be saved from text boxes before saving
     */
    private void preSave(){
        //get the title and note values based on either edit_mode or add_mode
        String name = (mPref.getBoolean("edit_mode",false) && isTitleEdited) ? mTitle.getText().toString() :
                (mPref.getBoolean("edit_mode",false) && !isTitleEdited) ? mOldTitle.toString() :  mTitle.getText().toString();
        String desc = (mPref.getBoolean("edit_mode",false) && isNoteEdited) ? mNote.getText().toString() :
                (mPref.getBoolean("edit_mode",false) && !isNoteEdited) ? mOldNote.toString() :  mNote.getText().toString();
        //String desc = (mPref.getBoolean("edit_mode",false) && isNoteEdited) ? mNote.getText().toString() : mOldNote.toString();

        if(!mPref.getBoolean("edit_mode",false) && name.isEmpty() && desc.isEmpty() ||
                !mPref.getBoolean("edit_mode",false) && !name.isEmpty() && desc.isEmpty() ||
                !mPref.getBoolean("edit_mode",false) && name.isEmpty() && !desc.isEmpty()){
            Toast.makeText(this, "Title and Note required!", Toast.LENGTH_SHORT).show();
        }else
        if(mPref.getBoolean("edit_mode",false) && mOldTitle.toString().equalsIgnoreCase(name)
                && mOldNote.toString().equalsIgnoreCase(desc)){
            Toast.makeText(this, "No change was made!", Toast.LENGTH_SHORT).show();
        }else
        if(mPref.getBoolean("edit_mode",false) && name.isEmpty() && desc.isEmpty() ||
                mPref.getBoolean("edit_mode",false) && !name.isEmpty() && desc.isEmpty() ||
                mPref.getBoolean("edit_mode",false) && name.isEmpty() && !desc.isEmpty()){
            Toast.makeText(this, "Can't leave either title or its note empty!", Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
           /* if(!isTitleEdited && !isNoteEdited) {
                String letter = mOldTitle.toString().substring(0,1);
                save(mOldTitle.toString(), mOldNote.toString(), letter.toUpperCase());
            }else
            if(!isTitleEdited && isNoteEdited) {
                String letter = mOldTitle.toString().substring(0,1);
                save(mOldTitle.toString(), desc, letter.toUpperCase());
            }else
            if(isTitleEdited && !isNoteEdited) {
                String letter = name.substring(0,1);
                save(name, mOldNote.toString(), letter.toUpperCase());
            }
            else{*/
                String letter = name.substring(0,1);
                save(name, desc, letter.toUpperCase());
            //}
        }
    }
    @Override
    public void onClick(View v) {
        if(v == mAddBtn){
            preSave();
        }
        if(v == mPlainName){
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(mPlainName.getText().toString());
            mPlainName.setVisibility(View.GONE);
            isTitleEdited = true;
        }
        if(v == mPlainNote){
            mNote.setVisibility(View.VISIBLE);
            mNote.setText(mPlainNote.getText().toString());
            mPlainNote.setVisibility(View.GONE);
            isNoteEdited = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein_a_bit,R.anim.slide_out_from_right);
        if(mPref.getBoolean("edit_mode",false)){
            //my preference
            SharedPreferences.Editor editor = mPref.edit();
            //remove edit_mode tag when back button is pressed while in Update page
            editor.remove("edit_mode");
            editor.apply();
            //Toast.makeText(JournalNoteActivity.this, "Journal Note Updated!", Toast.LENGTH_SHORT).show();
        }
    }
}
















