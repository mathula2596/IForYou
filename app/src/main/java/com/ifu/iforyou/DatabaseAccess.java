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
import java.text.DateFormat;
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
    private Calendar calendar;


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
            contentValues2.put("timeAlert","06:00");
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

    public Cursor getTimetableForStudents(String studentId){
        c = null;
        int userId = findUserIdByUniversityId(studentId);
        c = db.rawQuery("select timetable.id, timetable.date, timetable.startTime, timetable" +
                ".endTime, timetable" +
                ".moduleId, timetable.lecturerId, timetable.classType, timetable.location, users" +
                ".universityId, users.firstname, users.lastname, module.moduleName" +
                " " +
                "from timetable " +
                "inner join studentTimetable on timetable.id = studentTimetable.timetableId inner" +
                " join users on users.id = timetable.lecturerId inner join module on module.id = " +
                "timetable.moduleId" +
                " where " +
                "studentTimetable.studentId" +
                " = ? and " +
                "timetable.status" +
                " =?", new String[]{String.valueOf(userId),
                status});
        Log.d("TAG", "getTimetableForStudents: "+c.getCount());
        if(c.getCount()>0)
        {
            return c;
        }
            return c;
    }

    public Cursor getAlertByStudentId(String studentId){
        c = null;
        int userId = findUserIdByUniversityId(studentId);

        c = db.rawQuery("select timetable.id, alert.id as alertID, timetable.date, alert.message," +
                " alert.dateAlert as dateAlert, " +
                "alert.timeAlert as timeAlert, alert.type from timetable " +
                "inner join alert on timetable.id = alert.timetableId inner join " +
                "studentTimetable on studentTimetable.timetableId = timetable.id where " +
                "alert.status" +
                " = ? and studentTimetable.studentId = ?", new String[]{status,
                String.valueOf(userId),
                });
        Log.d("TAG", "getTimetableForStudents: "+c.getCount());
        if(c.getCount()>0)
        {
            return c;
        }
        return c;
    }

    public Cursor getAttendanceByStudentId(String studentId, String module){
        c = null;
        int userId = findUserIdByUniversityId(studentId);
        int moduleId = findModuleIdByName(module);

        c = db.rawQuery("select timetable.date as date, attendance.attendance from attendance " +
                "inner join timetable on timetable.id = " +
                "attendance.studentTimetableId" +
                " where attendance.studentId= ? and timetable.moduleId = ? and attendance.status " +
                "=?", new String[]{String.valueOf(userId),String.valueOf(moduleId),status});

        //Log.d("TAG", "getAttendanceByStudentId: "+c.getCount());
        if(c.getCount()>0)
        {
            return c;
        }
        return c;
    }

    public Cursor getModuleByStudentId(String studentId){
        c = null;
        int userId = findUserIdByUniversityId(studentId);

        c = db.rawQuery("select * from studentModule inner join module on module.id = " +
                "studentModule.moduleId" +
                " where studentModule.studentId= ? and " +
                "studentModule.status " +
                "=?", new String[]{String.valueOf(userId),status});

        if(c.getCount()>0)
        {
            return c;
        }
        return c;
    }
    public Cursor getOverallAttendanceByStudentId(String studentId, String moduleId){
        c = null;
        int userId = findUserIdByUniversityId(studentId);

        if(moduleId.equals("0"))
        {
            c = db.rawQuery("select * from attendance where studentId= ? and " +
                    "status " +
                    "=?", new String[]{String.valueOf(userId),status});
        }
        else
        {
            int moduleIdSearch = findModuleIdByName(moduleId);
            c = db.rawQuery("select * from attendance inner join timetable on timetable.id = " +
                    "attendance.studentTimetableId" +
                    " where attendance.studentId= ? and timetable.moduleId = ? and attendance" +
                    ".status " +
                    "=?", new String[]{String.valueOf(userId),String.valueOf(moduleIdSearch),status});
        }


        //Log.d("TAG", "getOverallAttendanceByStudentId: "+c);
        if(c.getCount()>0)
        {
            return c;
        }
        return c;
    }
    public String[] getCurrentWeek() {
        this.calendar = Calendar.getInstance();
        this.calendar.setFirstDayOfWeek(Calendar.MONDAY);
        this.calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return getNextWeek();
    }
    public String[] getNextWeek() {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String[] days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(this.calendar.getTime());
            this.calendar.add(Calendar.DATE, 1);
        }
        return days;
    }
    public String[] getPreviousWeek() {
        this.calendar.add(Calendar.DATE, -14);
        return getNextWeek();
    }
    public Cursor getLastWeekAttendanceByStudentId(String studentId, String moduleId){
        c = null;
        int userId = findUserIdByUniversityId(studentId);



        Log.d("tag" ,"Current : " + Arrays.toString(getCurrentWeek()));
        String [] getPreviousWeekArray = getPreviousWeek();

        if(moduleId.equals("0"))
        {
            c = db.rawQuery("select * from attendance inner join timetable on attendance" +
                    ".studentTimetableId = timetable.id where " +
                    "attendance.studentId=" +
                    " ? and timetable.date >= ? and timetable.date <= ? and "  +
                    "attendance.status " +
                    "=?", new String[]{String.valueOf(userId),getPreviousWeekArray[0],
                    getPreviousWeekArray[6],status});
        }
        else
        {
            int moduleIdSearch = findModuleIdByName(moduleId);
            c = db.rawQuery("select * from attendance inner join timetable on attendance" +
                    ".studentTimetableId = timetable.id where " +
                    "attendance.studentId=" +
                    " ? and timetable.moduleId= ? and timetable.date >= ? and timetable.date <= " +
                    "? and "  +
                    "attendance.status " +
                    "=?", new String[]{String.valueOf(userId),String.valueOf(moduleIdSearch),
                    getPreviousWeekArray[0],
                    getPreviousWeekArray[6],status});
        }

        Log.d("TAG",
                "getLastWeekAttendanceByStudentId: "+c.getCount());

        if(c.getCount()>0)
        {
            return c;
        }
        return c;
    }

   public Cursor getThisWeekAttendanceByStudentId(String studentId, String moduleId){
        c = null;
        int userId = findUserIdByUniversityId(studentId);

        String [] getThisWeekArray = getCurrentWeek();


       if(moduleId.equals("0"))
       {

           c = db.rawQuery("select * from attendance inner join timetable on attendance" +
                   ".studentTimetableId = timetable.id where " +
                   "attendance.studentId=" +
                   " ? and timetable.date >= ? and timetable.date <= ? and "  +
                   "attendance.status " +
                   "=?", new String[]{String.valueOf(userId),getThisWeekArray[0],
                   getThisWeekArray[6],status});
       }
       else
       {
           int moduleIdSearch = findModuleIdByName(moduleId);

           c = db.rawQuery("select * from attendance inner join timetable on attendance" +
                   ".studentTimetableId = timetable.id where " +
                   "attendance.studentId=" +
                   " ? and timetable.moduleId = ? and timetable.date >= ? and timetable.date <= ?" +
                   " and "  +
                   "attendance.status " +
                   "=?", new String[]{String.valueOf(userId),String.valueOf(moduleIdSearch),
                   getThisWeekArray[0],
                   getThisWeekArray[6],status});
       }




        if(c.getCount()>0)
        {
            return c;
        }
        return c;
    }

    public Cursor getLastWeekAttendanceByStudentIdForNotification(String studentId){
        c = null;
        int userId = findUserIdByUniversityId(studentId);


        Log.d("tag" ,"Current : " + Arrays.toString(getCurrentWeek()));
        String [] getPreviousWeekArray = getPreviousWeek();

        c = db.rawQuery("select * from attendance inner join timetable on attendance" +
                ".studentTimetableId = timetable.id where " +
                "attendance.studentId=" +
                " ? and timetable.date >= ? and timetable.date <= ? and "  +
                "attendance.status " +
                "=?", new String[]{String.valueOf(userId),getPreviousWeekArray[0],
                getPreviousWeekArray[6],status});
        int noOfClassesLastWeek = 0;
        float attendancePercentageLastWeek = 0;
        int attendedClassLastWeek = 0;


        if(c.getCount()>0)
        {
            while (c.moveToNext())
            {
                if(c.getString(c.getColumnIndex("attendance")).equals("1"))
                {
                    attendedClassLastWeek = attendedClassLastWeek + 1;
                }
            }

            attendancePercentageLastWeek =
                    (Float.parseFloat(String.valueOf(attendedClassLastWeek))/Float.parseFloat(String.valueOf(noOfClassesLastWeek)) * 100);
        }

        String message = "Consider your attendance";
        if(attendancePercentageLastWeek == 100)
        {
            message = "Excellent on your attendance";
        }
        else if(attendancePercentageLastWeek >= 75)
        {
            message = "Great keep going";
        }
        else if(attendancePercentageLastWeek >= 50)
        {
            message = "Mind your attendance";
        }
        else if(attendancePercentageLastWeek < 50)
        {
            message = "You must attend the class";
        }

        Cursor cursor = db.rawQuery("select * from alertAttendance where " +
                "studentId=" +
                " ?", new String[]{String.valueOf(userId)});

        //Log.d("TAG", "getLastWeekAttendanceByStudentIdForNotification: "+cursor);
        if(cursor != null && cursor.getCount() >0)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("message",message);
            contentValues.put("status",status);

            long result = db.update("alertAttendance",contentValues,"studentId = ?",
                    new String[]{String.valueOf(userId)});
        }
        else
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("studentId",userId);
            contentValues.put("message",message);
            contentValues.put("dayAlert","5");
            contentValues.put("timeAlert","13:00");
            contentValues.put("status",status);

            long result = db.insert("alertAttendance",null,contentValues);
        }
        c = db.rawQuery("select * from alertAttendance where " +
                "studentId=" +
                " ? and status " +
                "=?", new String[]{String.valueOf(userId),status});
        return c;
    }
    public Cursor getLastWeekAttendanceByStudentIdForNotificationUpdate(String studentId){
        c = null;
        int userId = findUserIdByUniversityId(studentId);
        ContentValues contentValues = new ContentValues();
        contentValues.put("studentId",userId);
        contentValues.put("status","Completed");

        Log.d("TAG", "getLastWeekAttendanceByStudentIdForNotificationUpdate: "+userId);
        long result = db.update("alertAttendance",contentValues,"studentId=?",
                new String[]{String.valueOf(userId)});
        return c;
    }

    public Cursor updateAlertByStudentId(String alertId){
        c = null;


       // Log.d("TAGA", "updateAlertByStudentId: " + alertId);
        if(!alertId.isEmpty())
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("status", "Completed");
            int r = db.update("alert", contentValues, " id =?",
                    new String[]{alertId});

        }



        return c;
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
