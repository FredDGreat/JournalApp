package com.ftech.journalapp.mDataBase;

/**
 * Created by Frederick
 */
public class Constants {
    //COLUMNS
    public static final String ROW_ID="id";
    public static final String NAME="name";
    public static final String DESC="desc";
    public static final String LETTER ="letter";
    public static final String DATE="date";
    public static final String COLOR="color";


    //DB PROPERTIES
    public static final String DB_NAME="hh_DB";
    public static final String TB_NAME="journal_TB";
    public static final int DB_VERSION=1;

    //CREATE TB STMT
    public static final String CREATE_TB="CREATE TABLE "+TB_NAME+"(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT NOT NULL,desc TEXT NOT NULL, letter TEXT NOT NULL,date TEXT NOT NULL,color TEXT NOT NULL);";

    //DROP TB STMT
    public static final String DROP_TB="DROP TABLE IF EXISTS "+TB_NAME;
}
