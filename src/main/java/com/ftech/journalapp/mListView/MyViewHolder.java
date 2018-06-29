package com.ftech.journalapp.mListView;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Frederick
 */
public class MyViewHolder implements View.OnLongClickListener,View.OnCreateContextMenuListener {

    public TextView mMedName;
    public TextView mDesc;
    public TextView mInterval;
    public TextView mDate;
    public MyLongClickListener longClickListener;

    public MyViewHolder(View v) {

        v.setOnLongClickListener(this);
        v.setOnCreateContextMenuListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        this.longClickListener.onItemLongClick();
        return false;
    }

    public void setLongClickListener(MyLongClickListener longClickListener)
    {
        this.longClickListener=longClickListener;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Action : ");
        //menu.add(0,0,0,"EDIT");
        menu.add(0,0,0,"DELETE");


    }
}
