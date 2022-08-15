package com.ifu.iforyou;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "IForYou.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseOpenHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
//        super(context, DATABASE_NAME,
//                context.getExternalFilesDir("../../com.ifu.iforyoustudent").getAbsolutePath(),
//         null, DATABASE_VERSION);

    }
}
