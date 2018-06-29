package com.ftech.journalapp.adapters;

/**
 * Created by Frederick.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ftech.journalapp.JournalNoteActivity;
import com.ftech.journalapp.R;
import com.ftech.journalapp.data.ListData;
import com.ftech.journalapp.animation.MyBounceInterpolator;
import com.ftech.journalapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private List<ListData> mListData;
    //individual item's icon color
    String[] colorArray = {"#9c0a90","#f64c74","#20d2cc","#4495ff","#6145a3","#d11515"};
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle, mDesc, mLetter,mDate;
        public String mColor;
        public RelativeLayout mItemBg,mLetterBg;
        public int mId;

        public MyViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.title);
            mDesc = (TextView) view.findViewById(R.id.desc);
            mLetter = (TextView) view.findViewById(R.id.letter);
            mDate = (TextView) view.findViewById(R.id.date);
            mItemBg = (RelativeLayout) view.findViewById(R.id.itemBg);
            mLetterBg = (RelativeLayout) view.findViewById(R.id.letterBg);
        }
    }


    public RecyclerAdapter(Context context, List<ListData> listData) {
        this.mContext = context;
        this.mListData = listData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_list_item, parent, false);

        return new MyViewHolder(itemView);
    }
    public void doBubbleAnimation(View view){
        //Button button = (Button)findViewById(R.id.button);
        final Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.buble_scale);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        view.startAnimation(myAnim);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ListData listData = mListData.get(position);
        holder.mTitle.setText(listData.getmTitle());
        holder.mDesc.setText(listData.getmDesc());
        holder.mLetter.setText(listData.getmLetter());
        holder.mDate.setText(listData.getmDate());
        holder.mId = listData.getId();
        //set different color from colorArray
        holder.mLetter.setTextColor(Color.parseColor(listData.getmLetterColor()));
        //holder.mLetter.setTextColor(Color.parseColor(colorArray[position]));
        holder.mColor = listData.getmLetterColor();
        //get the position
        final int pos = position;
        holder.mItemBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //my preference
                SharedPreferences mPref = mContext.getSharedPreferences("JOURNAL_NOTE_ADDED",mContext.MODE_PRIVATE);
                SharedPreferences.Editor editor = mPref.edit();
                editor.putBoolean("edit_mode",true);
                editor.apply();
                Intent intent1 = new Intent(mContext, JournalNoteActivity.class);
                //intent1.putExtra(Constants.NAV_HEADER_COLOR_TAG,colorArray[pos]);
                intent1.putExtra(Constants.ID,holder.mId);
                intent1.putExtra(Constants.TITLE,holder.mTitle.getText().toString());
                intent1.putExtra(Constants.DESC,holder.mDesc.getText().toString());
                intent1.putExtra(Constants.COLOR,holder.mColor);
                mContext.startActivity(intent1);
                /*switch(pos){
                    case 0:
                        holder.mItemState.setImageResource(R.mipmap.ic_active);
                        doBubbleAnimation(holder.mItemState);
                        break;
                    case 1:

                        break;
                    case 2:
                        //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, PatternLock.class);
                        intent.putExtra(Constants.NAV_HEADER_COLOR_TAG,colorArray[pos]);
                        mContext.startActivity(intent);
                        break;
                    case 3:
                        //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(mContext, PinLock.class);
                        intent1.putExtra(Constants.NAV_HEADER_COLOR_TAG,colorArray[pos]);
                        mContext.startActivity(intent1);
                        break;
                    case 4:
                        //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(mContext, PasswordLock.class);
                        intent2.putExtra(Constants.NAV_HEADER_COLOR_TAG,colorArray[pos]);
                        mContext.startActivity(intent2);
                        break;
                    case 5:

                        break;
                    default:;
                }*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }
}
