package com.ifu.iforyou;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;


public class EditTimetableFragment extends Fragment {
    private View view;

    private TextInputLayout txtDate, txtStartTime, txtEndTime,txtLocation;

    private AutoCompleteTextView lecturerId, type;
    private Button btnSubmit, btnStartTime, btnDate, btnEndTime;

    private String [] types = {"Online","InPerson"};

    private String status = "Active";
    private Bundle bundle;
    private String id = null;
    private int year, month, dayDate, hour, minute;
    Calendar calendar;
    private DatabaseAccess databaseAccess;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> lecturerList, lecturerName;
    private Cursor editDetails;
    private String moduleIds,lecturerIds, universityId = null;
    private FragmentTransaction fragmentTransaction;
    private ViewTimetableFragment viewTimetableFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_timetable, container, false);

        txtDate = view.findViewById(R.id.date);
        txtStartTime = view.findViewById(R.id.startTime);
        txtEndTime = view.findViewById(R.id.endTime);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnDate = view.findViewById(R.id.btnDate);
        btnStartTime = view.findViewById(R.id.btnStartTime);
        btnEndTime = view.findViewById(R.id.btnEndTime);
        txtLocation = view.findViewById(R.id.location);
        type = view.findViewById(R.id.type);
        lecturerId = view.findViewById(R.id.lecturerId);


        btnDate.setOnClickListener(view1 -> {
            calendar= Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayDate = calendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            int newMonth = monthOfYear + 1;
                            String twoDigitMonth = String.valueOf(newMonth);
                            String twoDigitDate = String.valueOf(dayOfMonth);
                            if(newMonth<10)
                            {
                                twoDigitMonth = "0"+newMonth;
                            }

                            if(dayOfMonth<10)
                            {
                                twoDigitDate = "0"+dayOfMonth;
                            }

                            txtDate.getEditText().setText(twoDigitDate + "-" + (twoDigitMonth) + "-" + year);

                        }
                    }, year, month, dayDate);
            datePickerDialog.show();
        });
        btnStartTime.setOnClickListener(view1 -> {
            calendar= Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            String newHoursOfDay = String.valueOf(hourOfDay);
                            if(hourOfDay<10)
                            {
                                newHoursOfDay = "0"+hourOfDay;
                            }

                            String newMinute = String.valueOf(minute);
                            if(minute<10)
                            {
                                newMinute = "0"+minute;
                            }
                            txtStartTime.getEditText().setText(newHoursOfDay + ":" + newMinute);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        btnEndTime.setOnClickListener(view1 -> {
            calendar= Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            String newHoursOfDay = String.valueOf(hourOfDay);
                            if(hourOfDay<10)
                            {
                                newHoursOfDay = "0"+hourOfDay;
                            }

                            String newMinute = String.valueOf(minute);
                            if(minute<10)
                            {
                                newMinute = "0"+minute;
                            }
                            txtEndTime.getEditText().setText(newHoursOfDay + ":" + newMinute);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });



        bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getString("id", "0");
        }

        databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        editDetails = databaseAccess.editTimetable(id);

        txtDate.getEditText().setText(editDetails.getString(editDetails.getColumnIndex("date")));
        txtStartTime.getEditText().setText(editDetails.getString(editDetails.getColumnIndex(
                "startTime")));
        txtEndTime.getEditText().setText(editDetails.getString(editDetails.getColumnIndex(
                "endTime")));
        txtLocation.getEditText().setText(editDetails.getString(editDetails.getColumnIndex(
                "location")));

        type.setText(editDetails.getString(editDetails.getColumnIndex(
                "classType")));

        moduleIds = editDetails.getString(editDetails.getColumnIndex("moduleId"));
        lecturerIds = editDetails.getString(editDetails.getColumnIndex("lecturerId"));

        universityId = editDetails.getString(editDetails.getColumnIndex(
                        "lecturerId"));


        databaseAccess.close();

        arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,types);
        type.setAdapter(arrayAdapter);

        databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        lecturerList = databaseAccess.findLecturerIdByModuleId(moduleIds);
        universityId = databaseAccess.findUniversityIdByUserId(universityId);
        lecturerId.setText(universityId);
        lecturerName = databaseAccess.lecturerId(lecturerList);
        arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,lecturerName);
        lecturerId.setAdapter(arrayAdapter);
        databaseAccess.close();

        btnSubmit.setOnClickListener(view1 -> {
            databaseAccess = DatabaseAccess.getInstance(getActivity());
            databaseAccess.open();
            boolean result = databaseAccess.updateTimetable(id,lecturerId.getText().toString(),
                    txtDate.getEditText().getText().toString(),
                    txtStartTime.getEditText().getText().toString(),
                    txtEndTime.getEditText().getText().toString(),
                    txtLocation.getEditText().getText().toString(),type.getText().toString());

            if(result)
            {
                Toast.makeText(getActivity(),"Updated Successful!",Toast.LENGTH_SHORT).show();
                viewTimetableFragment = new ViewTimetableFragment();
                replaceFragment(viewTimetableFragment);

            }
            else
            {
                Toast.makeText(getActivity(),"Updated Failed!",Toast.LENGTH_SHORT).show();
            }

            databaseAccess.close();

        });


        return view;
    }
    public void replaceFragment(Fragment fragment)
    {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}