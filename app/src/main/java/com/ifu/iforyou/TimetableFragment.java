package com.ifu.iforyou;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;


public class TimetableFragment extends Fragment {
    private View view;
    private Button btnSubmit, btnDate, btnStartTime, btnEndTime, btnTermStartDate, btnTermEndDate;
    private FragmentTransaction fragmentTransaction;
    private CheckBox chkRepeat;
    private TextInputLayout txtDate, txtStartTime, txtEndTime, txtTermStartDate, txtTermEndDate,
            txtLocation;
    private AutoCompleteTextView batchNumber, moduleId, lecturerId, type;
    private String [] types = {"Online","InPerson"};

    private String status = "Active";
    private ArrayList<String> batchName;
    private DatabaseAccess databaseAccess;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> moduleIds, moduleName, lecturerIds, lecturerName ;
    private int batchId, moduleIdRetrieve;
    private int year, month, dayDate, hour, minute;
    Calendar calendar;
    private LinearLayout linearLayout;
    private Boolean validField, validDropdown = false;
    private TimetableFragment timetableFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_timetable, container, false);

        batchNumber = view.findViewById(R.id.batchId);
        databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        batchName = databaseAccess.batchName();
        arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,batchName);
        batchNumber.setAdapter(arrayAdapter);
        databaseAccess.close();

        moduleId = view.findViewById(R.id.module);
        batchNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                moduleId.setText("");
                databaseAccess = DatabaseAccess.getInstance(getActivity());
                databaseAccess.open();
                batchId = databaseAccess.findBatchIdByName(batchNumber.getText().toString());
                moduleIds = databaseAccess.moduleId(String.valueOf(batchId));
                moduleName = databaseAccess.moduleName(moduleIds);
                arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,moduleName);
                moduleId.setAdapter(arrayAdapter);
                databaseAccess.close();
            }
        });

        lecturerId = view.findViewById(R.id.lecturerId);
        moduleId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lecturerId.setText("");
                databaseAccess = DatabaseAccess.getInstance(getActivity());
                databaseAccess.open();
                moduleIdRetrieve = databaseAccess.findModuleIdByName(moduleId.getText().toString());
                lecturerIds = databaseAccess.findLecturerIdByModuleId(String.valueOf(moduleIdRetrieve));
                lecturerName = databaseAccess.lecturerId(lecturerIds);
                arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,lecturerName);
                lecturerId.setAdapter(arrayAdapter);
                databaseAccess.close();
            }
        });

        btnDate = view.findViewById(R.id.btnDate);
        btnStartTime = view.findViewById(R.id.btnStartTime);
        btnEndTime = view.findViewById(R.id.btnEndTime);

        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnTermStartDate = view.findViewById(R.id.btnTermStartDate);
        btnTermEndDate = view.findViewById(R.id.btnTermEndDate);

        type = view.findViewById(R.id.type);
        arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,types);
        type.setAdapter(arrayAdapter);


        txtDate = view.findViewById(R.id.date);

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

                            txtDate.getEditText().setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, year, month, dayDate);
            datePickerDialog.show();
        });

        txtStartTime = view.findViewById(R.id.startTime);
        txtEndTime = view.findViewById(R.id.endTime);

        txtLocation = view.findViewById(R.id.location);
        txtTermStartDate = view.findViewById(R.id.termStartDate);
        txtTermEndDate = view.findViewById(R.id.termEndDate);

        btnStartTime.setOnClickListener(view1 -> {
            calendar= Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtStartTime.getEditText().setText(hourOfDay + ":" + minute);
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

                            txtEndTime.getEditText().setText(hourOfDay + ":" + minute);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        chkRepeat = view.findViewById(R.id.repeat);
        linearLayout = view.findViewById(R.id.repeatBorder);
        chkRepeat.setOnClickListener(view1 -> {
            if(chkRepeat.isChecked())
            {
                linearLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                linearLayout.setVisibility(View.GONE);
                txtTermStartDate.getEditText().setText("");
                txtTermEndDate.getEditText().setText("");
            }
        });

        btnTermStartDate.setOnClickListener(view1 -> {
            calendar= Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayDate = calendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtTermStartDate.getEditText().setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, year, month, dayDate);
            datePickerDialog.show();
        });

        btnTermEndDate.setOnClickListener(view1 -> {
            calendar= Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayDate = calendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtTermEndDate.getEditText().setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, year, month, dayDate);
            datePickerDialog.show();
        });

        btnSubmit.setOnClickListener(view1 -> {
           if(checkDropDownEmpty(batchNumber) && checkDropDownEmpty(moduleId) && checkDropDownEmpty(lecturerId) && checkFieldEmpty(txtDate)&&checkFieldEmpty(txtStartTime)&&checkFieldEmpty(txtEndTime)&&checkFieldEmpty(txtLocation)&&checkDropDownEmpty(type))
           {
               databaseAccess = DatabaseAccess.getInstance(getActivity());
               databaseAccess.open();
               boolean result = databaseAccess.insertTimetable(batchNumber.getText().toString()
                       ,moduleId.getText().toString(),lecturerId.getText().toString(),
                       txtDate.getEditText().getText().toString(),
                       txtStartTime.getEditText().getText().toString(),
                       txtEndTime.getEditText().getText().toString(),
                       txtLocation.getEditText().getText().toString(),type.getText().toString(),
                       txtTermStartDate.getEditText().getText().toString(),
                       txtTermEndDate.getEditText().getText().toString());
               databaseAccess.close();
               if(result)
               {
                   Toast.makeText(getActivity(),"Timetable Created!",Toast.LENGTH_SHORT).show();
                   timetableFragment = new TimetableFragment();
                   replaceFragment(timetableFragment);

               }
               else
               {
                   Toast.makeText(getActivity(),"Failed to Create Timetable!",Toast.LENGTH_SHORT).show();
               }
           }
        });
        return view;
    }
    public void replaceFragment(Fragment fragment)
    {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    public boolean checkFieldEmpty(TextInputLayout textField){
        validField = false;
        if(!textField.getEditText().getText().toString().isEmpty())
        {
            textField.setError(null);
            validField = true;
        }
        else
        {
            textField.setError("Please fill the value");
            validField = false;
        }
        return validField;
    }
    public boolean checkDropDownEmpty(AutoCompleteTextView autoCompleteTextView){
        validDropdown = false;
        if(autoCompleteTextView.getVisibility() == View.VISIBLE){
            if(!autoCompleteTextView.getText().toString().isEmpty())
            {
                autoCompleteTextView.setError(null);
                validDropdown = true;
            }
            else
            {
                autoCompleteTextView.setError("Please select the value");
                validDropdown = false;
            }
        }
        else
        {
            validDropdown = true;
        }
        return validDropdown;
    }
}