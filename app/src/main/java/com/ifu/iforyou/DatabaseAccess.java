package com.ifu.iforyou;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.Toast;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private  static DatabaseAccess instance;
    Cursor c = null;

    private DatabaseAccess(Context context)
    {
        this.openHelper = new DatabaseOpenHelper(context);

    }
    public static DatabaseAccess getInstance(Context context)
    {
        if(instance==null)
        {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    //opening the db
    public void open(){
        this.db=openHelper.getWritableDatabase();
    }

    //closing the db
    public void close()
    {
        if(db!=null)
        {
            db.close();
        }
    }

    public boolean register(String universityId, String firstname, String lastname, String email,
                         String password, String userRole, String courseId, String degreeLevel,
                         String batchNo, String status) throws GeneralSecurityException {

        String encryptedPassword = AESCrypt.encrypt(universityId,password);
        ContentValues contentValues = new ContentValues();
        contentValues.put("universityId",universityId);
        contentValues.put("firstName",firstname);
        contentValues.put("lastName",lastname);
        contentValues.put("email",email);
        contentValues.put("password",encryptedPassword);
        contentValues.put("userRole",userRole);
        contentValues.put("courseId",courseId);
        contentValues.put("degreeLevel",degreeLevel);
        contentValues.put("batchNo",batchNo);
        contentValues.put("status",status);

        long result = db.insert("users", null,contentValues);
        if(result==-1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /*public void decrypt(View view) throws GeneralSecurityException {
        String encrpyted = AESCrypt.decrypt(et_key.getText().toString(), et_value.getText().toString());
        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("lable", encrpyted);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Your Message Was Copied to clip board", Toast.LENGTH_SHORT).show();
        et_value.setText("");
        et_key.setText("");
        message.setText(String.format("You Decrypted key is (Copied To Clipboard) : %s", encrpyted));


    }*/

}
