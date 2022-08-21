package com.ifu.iforyou;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
    private ContentValues contentValues2;
    private String nextDate = null;
    private SimpleDateFormat simpleDateFormat;

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

    public ArrayList<String> courseName(String degreeLevel)
    {
        ArrayList<String> courseName = new ArrayList<>();
        c = db.rawQuery("select * from course where degreeLevel = ? and status = ?",
                new String[]{degreeLevel, status});

        if(c.getCount()>0)
        {

            while (c.moveToNext()){
                courseName.add(c.getString(c.getColumnIndex("courseName")));

            }
        }
        return courseName;
    }

    public ArrayList<String> batchName()
    {
        ArrayList<String> batchName = new ArrayList<>();
        c = db.rawQuery("select * from batch where status = ?",
                new String[]{status});

        if(c.getCount()>0)
        {

            while (c.moveToNext()){
                batchName.add(c.getString(c.getColumnIndex("name")));

            }
        }
        return batchName;
    }

    public ArrayList<String> moduleId(String batchId)
    {
       // Log.d("TAG", "batchId: "+batchId);
        ArrayList<String> moduleId = new ArrayList<>();
        c = db.rawQuery("select * from batchModule where batchId = ? and status = ?",
                new String[]{batchId, status});

        if(c.getCount()>0)
        {

            while (c.moveToNext()){
               // Log.d("TAG", "moduleId: "+c.getString(c.getColumnIndex("moduleId")));
                moduleId.add(c.getString(c.getColumnIndex("moduleId")));

            }
        }
        return moduleId;
    }

    public ArrayList<String> moduleName(ArrayList<String> moduleId)
    {
        ArrayList<String> moduleName = new ArrayList<>();
        String[] moduleIdArray = moduleId.toArray(new String[moduleId.size()]);
        //Log.d("TAG1", "moduleName: "+moduleIdArray);
        c =
                db.rawQuery("select * from module where id IN ("+ Arrays.toString(moduleIdArray).substring(1,Arrays.toString(moduleIdArray).length()-1)+
                        ")" +
                        " and status " +
                        "= ?",
                new String[]{status});

        if(c.getCount()>0)
        {
            while (c.moveToNext()){
                moduleName.add(c.getString(c.getColumnIndex("moduleName")));

            }
        }
        return moduleName;
    }
    public int findCourseIdByName(String degreeLevel, String courseName){
        int courseId = 0;
        c = db.rawQuery("select * from course where degreeLevel = ? and courseName = ? and status" +
                        " = ?",
                new String[]{degreeLevel,courseName, status});

        if(c.getCount()>0)
        {

            while (c.moveToNext()){
                courseId =  Integer.parseInt(c.getString(c.getColumnIndex("id")));
            }
        }
       return courseId;
    }

    public int findModuleIdByName(String moduleName){
        int moduleId = 0;
        c = db.rawQuery("select * from module where moduleName = ? and status" +
                        " = ?",
                new String[]{moduleName, status});

        if(c.getCount()>0)
        {
            while (c.moveToNext()){
                moduleId =  Integer.parseInt(c.getString(c.getColumnIndex("id")));
            }
        }
        return moduleId;
    }

    public ArrayList<Cursor> findTimetableByModuleIdAndBatchId(String moduleId, String batchId){

        ArrayList<Cursor> timeTable = new ArrayList<>();
        c = db.rawQuery("select timetable.id,timetable.date,timetable.startTime,timetable" +
                        ".endTime,timetable.classType,timetable.location,users.universityId" +
                        " from timetable inner join users on users.id = timetable" +
                        ".lecturerId" +
                        " where timetable.moduleId = ?  and timetable.batchNo =? and timetable" +
                        ".status" +
                        " = ?",
                new String[]{moduleId,batchId, status});

        if(c.getCount()>0)
        {
            while (c.moveToNext()){
                timeTable.add(c);
            }
        }
        return timeTable;
    }

    public ArrayList<String> findLecturerIdByModuleId(String moduleId){
        ArrayList <String> lecturerId = new ArrayList<>();
        c = db.rawQuery("select * from lecturerModule where moduleId  = ? and status" +
                        " = ?",
                new String[]{moduleId, status});

        if(c.getCount()>0)
        {
            while (c.moveToNext()){
                lecturerId.add(c.getString(c.getColumnIndex("lecturerId")));
            }
        }
        return lecturerId;
    }

    public ArrayList<String> lecturerId(ArrayList<String> lecturerId)
    {
        ArrayList<String> lecturerName = new ArrayList<>();
        String[] lecturerIdArray = lecturerId.toArray(new String[lecturerId.size()]);
        Log.d("TAG11", "moduleName: "+lecturerIdArray[0]);
        c =
                db.rawQuery("select * from users where id IN ("+ Arrays.toString(lecturerIdArray).substring(1,Arrays.toString(lecturerIdArray).length()-1)+
                                ")" +
                                " and status " +
                                "= ?",
                        new String[]{status});

        if(c.getCount()>0)
        {
            while (c.moveToNext()){
                lecturerName.add(c.getString(c.getColumnIndex("universityId")));

            }
        }
        return lecturerName;
    }
    public int findBatchIdByName(String batchName){
        int batchId = 0;
        c = db.rawQuery("select * from batch where name = ? and status" +
                        " = ?",
                new String[]{batchName, status});

        if(c.getCount()>0)
        {

            while (c.moveToNext()){
                batchId =  Integer.parseInt(c.getString(c.getColumnIndex("id")));
            }
        }
        return batchId;
    }

    public int findUserIdByUniversityId(String universityId){
        int userId = 0;
        c = db.rawQuery("select * from users where universityId = ? and status" +
                        " = ?",
                new String[]{universityId, status});

        if(c.getCount()>0)
        {

            while (c.moveToNext()){
                userId =  Integer.parseInt(c.getString(c.getColumnIndex("id")));
            }
        }
        return userId;
    }

    public String findUniversityIdByUserId(String id){
        String universityId = null;
        c = db.rawQuery("select * from users where id = ? and status" +
                        " = ?",
                new String[]{id, status});

        if(c.getCount()>0)
        {

            while (c.moveToNext()){
                universityId =  c.getString(c.getColumnIndex("universityId"));
            }
        }
        return universityId;
    }

    public ArrayList<String> findStudentIdFromBatch(String batchId){
        ArrayList<String> studentId = new ArrayList<>();
        c = db.rawQuery("select * from users where batchNo = ? and status" +
                        " = ?",
                new String[]{batchId, status});

        if(c.getCount()>0)
        {

            while (c.moveToNext()){
                studentId .add(c.getString(c.getColumnIndex("id")));
            }
        }
        return studentId;
    }

    public boolean findModuleForStudents(String studentId, String moduleId){
        c = db.rawQuery("select * from studentModule where studentId = ? and  moduleId = ? and " +
                        "status" +
                        " = ?",
                new String[]{studentId,moduleId, status});

        if(c.getCount()>0)
        {

            while (c.moveToNext()){
                return true;
            }
        }
        return false;
    }

    public boolean register(String universityId, String firstname, String lastname, String email,
                         String password, String userRole, String courseId, String degreeLevel,
                         String batchNo, String status) throws GeneralSecurityException {

        int course = findCourseIdByName(degreeLevel,courseId);
        int batch = findBatchIdByName(batchNo);
        encryptedPassword = AESCrypt.encrypt(universityId,password);
        ContentValues contentValues = new ContentValues();
        contentValues.put("universityId",universityId);
        contentValues.put("firstName",firstname);
        contentValues.put("lastName",lastname);
        contentValues.put("email",email);
        contentValues.put("password",encryptedPassword);
        contentValues.put("userRole",userRole);
        contentValues.put("courseId",course);
        contentValues.put("degreeLevel",degreeLevel);
        contentValues.put("batchNo",batch);
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

    public boolean updateTimetable(String id, String lecturerId,
                                   String date,
                                   String startTime, String endTime, String location, String type)
    {

        Cursor oldRecord = editTimetable(id);
        String oldDate, oldStartTime, oldEndTime, oldLocation, oldType, oldLecturerId = null;
        oldDate = oldRecord.getString(oldRecord.getColumnIndex("date"));
        oldStartTime = oldRecord.getString(oldRecord.getColumnIndex("startTime"));
        oldEndTime = oldRecord.getString(oldRecord.getColumnIndex("endTime"));
        oldLocation = oldRecord.getString(oldRecord.getColumnIndex("location"));
        oldType = oldRecord.getString(oldRecord.getColumnIndex("classType"));
        oldLecturerId = oldRecord.getString(oldRecord.getColumnIndex("lecturerId"));
        int lecturer = findUserIdByUniversityId(lecturerId);

        String [] oldValues = new String[]{oldDate,oldStartTime,oldEndTime,oldLocation,oldType,oldLecturerId};
        String [] newValues = new String[]{date,startTime,endTime,location,type,String.valueOf(lecturer)};
        String [] fields = new String[]{"Date","Start Time","End Time","Location","Class Type",
                "Lecturer"};
        String changedValues = "";

        for(int i = 0; i<oldValues.length; i++)
        {
            if(!oldValues[i].equals(newValues[i]))
            {
                changedValues = changedValues + fields[i]+ ", ";
            }
        }


        if(!String.valueOf(lecturer).equals(oldLecturerId) || !oldDate.equals(date) || !oldStartTime.equals(startTime) || ! oldEndTime.equals(endTime) || !oldLocation.equals(location) || ! oldType.equals(type) )
        {
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("timetableId",id);
            contentValues2.put("message",
                    "New updated on timetable on the followings: " + changedValues);
            contentValues2.put("fieldChanged",changedValues);
            contentValues2.put("dateAlert",date);
            contentValues2.put("timeAlert","6:0");
            contentValues2.put("type","Timetable");
            contentValues2.put("status",status);
            long result2 = db.insert("alert",null, contentValues2);
        }


        ContentValues contentValues = new ContentValues();
        contentValues.put("lecturerId",lecturer);
        contentValues.put("date",date);
        contentValues.put("startTime",startTime);
        contentValues.put("endTime",endTime);
        contentValues.put("classType",type);
        contentValues.put("location",location);
        contentValues.put("status",status);


        long result = db.update("timetable", contentValues,"id = ?",new String[]{id});

        if(result==-1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    public boolean insertTimetable(String batchId, String moduleId, String lecturerId,
                                String date,
                            String startTime, String endTime, String location, String type,
                             String termStartDate, String termEndDate) {

        if(termStartDate.isEmpty())
        {
            int module = findModuleIdByName(moduleId);
            int batch = findBatchIdByName(batchId);
            int lecturer = findUserIdByUniversityId(lecturerId);

            ContentValues contentValues = new ContentValues();
            contentValues.put("moduleId",module);
            contentValues.put("lecturerId",lecturer);
            contentValues.put("date",date);
            contentValues.put("startTime",startTime);
            contentValues.put("endTime",endTime);
            contentValues.put("classType",type);
            contentValues.put("location",location);
            contentValues.put("batchNo",batch);
            contentValues.put("status",status);


            long result = db.insert("timetable", null,contentValues);

            if(result==-1)
            {
                return false;
            }
            else
            {
                ArrayList<String> studentList = findStudentIdFromBatch(String.valueOf(batch));
                String[] studentIdArray = studentList.toArray(new String[studentList.size()]);
                for(int i = 0; i< studentIdArray.length; i++)
                {
                    if(findModuleForStudents(studentIdArray[i],String.valueOf(module)))
                    {
                        contentValues2 = new ContentValues();
                        contentValues2.put("studentId",studentIdArray[i].toString());
                        contentValues2.put("timetableId",String.valueOf(result));
                        contentValues2.put("status",status);

                        long result2 = db.insert("studentTimetable", null,contentValues2);
                    }
                }
                return true;
            }
        }
        else
        {
            Log.d("TAG1", "insertTimetable: "+termEndDate);
            Log.d("TAG1", "insertTimetable: "+termStartDate);

            Date startDateTerm = null, endDateTerm = null;
            simpleDateFormat = new SimpleDateFormat("d-M-yyyy");
            String previousDate = null, lastDate = null;
            Calendar cal = Calendar.getInstance();
            try {



                cal.setTime(simpleDateFormat.parse(termStartDate));
                cal.add(Calendar.DATE, -1);
                previousDate = simpleDateFormat.format(cal.getTime());
                startDateTerm = simpleDateFormat.parse(previousDate);


                cal = Calendar.getInstance();
                cal.setTime(simpleDateFormat.parse(termEndDate));
                cal.add(Calendar.DATE, 1);
                lastDate = simpleDateFormat.format(cal.getTime());
                endDateTerm = simpleDateFormat.parse(lastDate);

            } catch (ParseException e) {
            }
            long differenceWeek = endDateTerm.getTime() - startDateTerm.getTime();
            int weeks = (int)(TimeUnit.DAYS.convert(differenceWeek, TimeUnit.MILLISECONDS)/7);


            for(int i = 0 ; i<= weeks+1 ; i++)
            {
                Calendar c = Calendar.getInstance();
                try {
                    if(simpleDateFormat.parse(date).after(startDateTerm) && simpleDateFormat.parse(date).before(endDateTerm))
                    {
                        int module = findModuleIdByName(moduleId);
                        int batch = findBatchIdByName(batchId);
                        int lecturer = findUserIdByUniversityId(lecturerId);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("moduleId",module);
                        contentValues.put("lecturerId",lecturer);
                        contentValues.put("date",date);
                        contentValues.put("startTime",startTime);
                        contentValues.put("endTime",endTime);
                        contentValues.put("classType",type);
                        contentValues.put("location",location);
                        contentValues.put("batchNo",batch);
                        contentValues.put("status",status);


                        long result = db.insert("timetable", null,contentValues);

                        if(result==-1)
                        {
                            return false;
                        }
                        else
                        {
                            ArrayList<String> studentList = findStudentIdFromBatch(String.valueOf(batch));
                            String[] studentIdArray = studentList.toArray(new String[studentList.size()]);
                            for(int x = 0; x< studentIdArray.length; x++)
                            {
                                if(findModuleForStudents(studentIdArray[x],String.valueOf(module)))
                                {
                                    contentValues2 = new ContentValues();
                                    contentValues2.put("studentId",studentIdArray[x].toString());
                                    contentValues2.put("timetableId",String.valueOf(result));
                                    contentValues2.put("status",status);

                                    long result2 = db.insert("studentTimetable", null,contentValues2);
                                }
                            }

                        }
                    }
                    try {
                        c.setTime(simpleDateFormat.parse(date));
                    } catch (ParseException e) {
                    }

                    c.add(Calendar.DATE, 7);
                    date = simpleDateFormat.format(c.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

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

    public Cursor editTimetable(String id) {
        c = null;
        c = db.rawQuery("select * from timetable where id = ?", new String[]{id});
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
