package com.ifu.iforyou;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DatabaseAccess extends AppCompatActivity {
    public SQLiteOpenHelper openHelper;
    public SQLiteDatabase db;
    private  static DatabaseAccess instance;
    private Cursor c = null;
    private String status = "Active";
    String encryptedPassword,randomPassword = null;
    final String username = "mathula2011@gmail.com";
    final String password = "jmwwdizaqbgabsse";
    private Random random;

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

    public void adminAccountCreation() throws GeneralSecurityException {

        encryptedPassword = AESCrypt.encrypt("N0000000","123456");
        c = db.rawQuery("select * from users where userRole = ? and status =" +
                " ?", new String[]{"Admin", status});

       if(c.getCount()<=0)
        {
            ContentValues contentValues = new ContentValues();

            contentValues.put("universityId","N0000000");
            contentValues.put("firstName","Admin");
            contentValues.put("lastName","Admin");
            contentValues.put("email","admin@gmail.com");
            contentValues.put("password",encryptedPassword);
            contentValues.put("userRole","Admin");
            contentValues.put("status",status);

            long result = db.insert("users", null,contentValues);

        }

    }

    public boolean register(String universityId, String firstname, String lastname, String email,
                         String password, String userRole, String courseId, String degreeLevel,
                         String batchNo, String status) throws GeneralSecurityException {

        encryptedPassword = AESCrypt.encrypt(universityId,password);
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

    public String login(String username, String password) throws GeneralSecurityException {
        c = null;
        encryptedPassword = AESCrypt.encrypt(username,password);
        c = db.rawQuery("select * from users where universityId = ? and password = ? and status =" +
                        " ?", new String[]{username,encryptedPassword, status});
        if(c.getCount()>0)
        {
            while (c.moveToNext()){
                return c.getString(6);
            }
        }
        else
        {
            return "false";
        }

        return "false";
    }
    public Cursor studentLogin(String username, String password) throws GeneralSecurityException {
        c = null;
        String userRole = "Student";
        encryptedPassword = AESCrypt.encrypt(username,password);
        c = db.rawQuery("select * from users where universityId = ? and password = ? and status =" +
                " ? and userRole=?", new String[]{username,encryptedPassword, status,userRole});
        if(c.getCount()>0)
        {
            while (c.moveToNext()){
                return c;
            }
        }
        return c;
    }

    public boolean forgotPasswordEmailCheck(String emailId){
        c = null;
        c = db.rawQuery("select * from users where email = ? and status =?", new String[]{emailId,
                status});
        if(c.getCount()>0)
        {
            return  true;
        }
        else
        {
            return false;
        }
    }

    public Cursor findByEmail(String emailId){
        c = null;
        c = db.rawQuery("select * from users where email = ? and status =?", new String[]{emailId,
                status});
       return c;
    }


    public boolean forgotPasswordUpdatePassword(String emailId, String password) throws GeneralSecurityException {
        c = null;
        c = db.rawQuery("select * from users where email = ? and status =?", new String[]{emailId,
                status});
        if(c.getCount()>0)
        {
            String universityId = null;
            while (c.moveToNext()){
                universityId = c.getString(c.getColumnIndex("universityId"));
            }
            Log.d("TAG", "forgotPasswordUpdatePassword: "+password);
            ContentValues contentValues = new ContentValues();
            contentValues.put("password",AESCrypt.encrypt(universityId,password));
            db.update("users",contentValues," email =?",new String[]{emailId});
            return  true;
        }
        else
        {
            return false;
        }
    }

    public boolean sendPassword(String email)
    {
        Properties props = new Properties();
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
            message.setSubject("Reset Password");
            random = new Random();
            randomPassword = String.format("%04d", random.nextInt(10000));
            boolean result2 =
                    forgotPasswordUpdatePassword(email,randomPassword);
            if(result2){
                message.setText("New Password - " + randomPassword);
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Transport.send(message);
                return true;

            }
            else
            {
                return false;
            }


        }
        catch (MessagingException | GeneralSecurityException e)
        {
            throw new RuntimeException(e);
        }
    }


}
