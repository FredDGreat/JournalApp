package com.ftech.journalapp.mDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Frederick
 */
public class DBAdapter {

    Context c;
    SQLiteDatabase db;
    DBHelper helper;

    public DBAdapter(Context c) {
        this.c = c;
        helper=new DBHelper(c);
    }

    /**
     * opens the database for a writable operation
     */
    public void openDB()
    {
        try
        {
            db=helper.getWritableDatabase();
        }catch (SQLException e)
        {

        }
    }

    /**
     * closes the database
     */
    public void closeDB()
    {
        try
        {
            helper.close();
        }catch (SQLException e)
        {

        }
    }


    /**
     * @param name journal note title
     * @param desc the journal note
     * @param letter the first letter of the title.
     * @param date the date and time when the note was added
     * @param color the color of the first letter of the title
     * @return saves sData to database
     */
    public boolean add(String name,String desc,String letter,String date,String color)
    {
        try
        {
            ContentValues cv=new ContentValues();
            cv.put(Constants.NAME,name);
            cv.put(Constants.DESC,desc);
            cv.put(Constants.LETTER,letter);
            cv.put(Constants.DATE,date);
            cv.put(Constants.COLOR,color);

            long result=db.insert(Constants.TB_NAME,Constants.ROW_ID,cv);
            if(result>0)
            {
                return true;
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * @return retrieves sData from the database
     */
    public Cursor retrieve()
    {
        String[] columns={Constants.ROW_ID,Constants.NAME,Constants.DESC,Constants.LETTER,Constants.DATE,Constants.COLOR};

        Cursor c=db.query(Constants.TB_NAME,columns,null,null,null,null,null);
        return c;
    }
    public Cursor getJournalListByKeyword(String search) {
        //Open connection to read only
        //SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  id as " +
                Constants.ROW_ID + "," +
                Constants.NAME + "," +
                Constants.DESC + "," +
                Constants.LETTER + "," +
                Constants.DATE +
                " FROM " + Constants.TB_NAME +
                " WHERE " +  Constants.NAME + " LIKE '%" +search + "%' "
                ;
        /*Cursor c;
        c=db.rawQuery("SELECT * FROM "+ Constants.TB_NAME + " WHERE "
                + Constants.ROW_ID + " = " + Constants.ROW_ID + " AND " + DB_NAME.Title +
                " LIKE '%"+search+"%'");*/

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;


    }

    /**
     * Updates an exisiting data item in the database
     * @param newName new Journal name
     * @param newDesc new Journal description
     * @param newLetter the first letter of the title.
     * @param newDate the date and time when the note was added
     * @param color the color of the first letter of the title
     * @param id the id for each row in the database
     * @return updates medication sData
     */
    public boolean update(String newName,String newDesc,String newLetter,String newDate,String color,int id)
    {
        try
        {
            ContentValues cv=new ContentValues();
            cv.put(Constants.NAME,newName);
            cv.put(Constants.DESC,newDesc);
            cv.put(Constants.LETTER,newLetter);
            cv.put(Constants.DATE,newDate);
            cv.put(Constants.COLOR,color);


            int result=db.update(Constants.TB_NAME,cv, Constants.ROW_ID + " =?", new String[]{String.valueOf(id)});
            if(result>0)
            {
                return true;
            }
        }catch (SQLException e)
        {
             e.printStackTrace();
        }

        return false;

    }

    //DELETE/REMOVE

    /**
     * @param id id for the sData item
     * @return deletes/removes sData item from database using its id
     */
    public boolean delete(int id)
    {
        try
        {
            int result=db.delete(Constants.TB_NAME,Constants.ROW_ID+" =?",new String[]{String.valueOf(id)});
            if(result>0)
            {
                return true;
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }


}