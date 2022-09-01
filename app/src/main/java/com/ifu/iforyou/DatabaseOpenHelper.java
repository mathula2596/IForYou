package com.ifu.iforyou;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "IForYou.db";
    private static final int DATABASE_VERSION = 57;

    public DatabaseOpenHelper(Context context){

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        setForcedUpgrade(); // upgrade the database. data loss will occur
    }
}
