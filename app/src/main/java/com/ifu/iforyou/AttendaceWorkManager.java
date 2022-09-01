package com.ifu.iforyou;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AttendaceWorkManager extends Worker {
    public AttendaceWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        //Log.d("TAG", "doWork: "+"log");

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        databaseAccess.calculateLastWeekAttendance();
        databaseAccess.close();

        return null;
    }
}
