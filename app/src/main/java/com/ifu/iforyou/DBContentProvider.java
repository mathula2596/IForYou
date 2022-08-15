package com.ifu.iforyou;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DBContentProvider extends ContentProvider {
    public DBContentProvider() {
    }

    static final String PROVIDER_NAME = "com.ifu.contentprovider.provider";

    static final int uriCode = 1;
    static final UriMatcher uriMatcher;
    private DatabaseAccess databaseAccess;
    private Random random;
    final String username = "mathula2011@gmail.com";
    final String password = "jmwwdizaqbgabsse";

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "users", uriCode);
        uriMatcher.addURI(PROVIDER_NAME, "users/*", uriCode);
        uriMatcher.addURI(PROVIDER_NAME, "login", uriCode);
        uriMatcher.addURI(PROVIDER_NAME, "forgotpassword", uriCode);
    }
    private Cursor cursor = null;
    Context context;
    String randomPassword = null;

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case uriCode:
                return "vnd.android.cursor.dir/users";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        context = getContext();
        databaseAccess = DatabaseAccess.getInstance(getContext());
        databaseAccess.openHelper = new DatabaseOpenHelper(context);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        cursor = null;
        if (uri.equals(Uri.parse("content://" + PROVIDER_NAME + "/login"))) {

            databaseAccess.open();
            try {
                cursor = databaseAccess.studentLogin(selectionArgs[0],selectionArgs[1]);
                databaseAccess.close();
                return cursor;

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            databaseAccess.close();
        }
        else if(uri.equals(Uri.parse("content://" + PROVIDER_NAME + "/forgotpassword")))
        {
            databaseAccess.open();
            boolean result = false;
            result =
                    databaseAccess.forgotPasswordEmailCheck(selectionArgs[0]);

            if(result)
            {
                if(databaseAccess.sendPassword(selectionArgs[0]))
                {
                   cursor = databaseAccess.findByEmail(selectionArgs[0]);
                }

            }

            return cursor;
        }
       return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
       return uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        return count;
    }

}