package com.ifu.iforyou;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewTimetableFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<Timetable> dataHolder;
    private AutoCompleteTextView batchId, moduleId;
    private Button btnSearch;
    private boolean validDropdown = false;
    private ArrayList<String> batchName;
    private ArrayList<String> moduleIds, moduleName;

    private DatabaseAccess databaseAccess;
    private ArrayAdapter<String> arrayAdapter;
    private int batchIdNumber, moduleIdRetrieve;
    private ArrayList<Cursor> cursorArrayList;
    private Cursor cursor;
    private FragmentTransaction fragmentTransaction;
    private EditTimetableFragment editTimetableFragment;

    private RecyclerViewAdapter.RecyclerViewClickListener listener;

    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_view_timetable, container, false);
        batchId = view.findViewById(R.id.batchId);
        moduleId = view.findViewById(R.id.moduleId);
        btnSearch = view.findViewById(R.id.btnSearch);

        databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        batchName = databaseAccess.batchName();
        arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,batchName);
        batchId.setAdapter(arrayAdapter);
        databaseAccess.close();

        batchId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                moduleId.setText("");
                databaseAccess = DatabaseAccess.getInstance(getActivity());
                databaseAccess.open();
                batchIdNumber = databaseAccess.findBatchIdByName(batchId.getText().toString());
                moduleIds = databaseAccess.moduleId(String.valueOf(batchIdNumber));
                moduleName = databaseAccess.moduleName(moduleIds);
                arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,moduleName);
                moduleId.setAdapter(arrayAdapter);
                databaseAccess.close();
            }
        });

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataHolder=new ArrayList<>();

        btnSearch.setOnClickListener(view1 -> {
            if(checkDropDownEmpty(batchId)&& checkDropDownEmpty(moduleId))
            {
                databaseAccess = DatabaseAccess.getInstance(getActivity());
                databaseAccess.open();

                batchIdNumber = databaseAccess.findBatchIdByName(batchId.getText().toString());
                moduleIdRetrieve = databaseAccess.findModuleIdByName(moduleId.getText().toString());

                cursorArrayList =
                        databaseAccess.findTimetableByModuleIdAndBatchId(String.valueOf(batchIdNumber),String.valueOf(moduleIdRetrieve));
                databaseAccess.close();
                Cursor[] cursorArray = cursorArrayList.toArray(new Cursor[cursorArrayList.size()]);
                Timetable timetableObject;
                for(int i = 0 ; i <cursorArrayList.size(); i++)
                {
                    cursor = cursorArray[i];
                    if(i == 0)
                    {
                        cursor.moveToFirst();
                    }
                    if(cursor.getCount()>0)
                    {

                        while (cursor.moveToNext()){

                            timetableObject=new Timetable(cursor.getString(cursor.getColumnIndex(
                                    "date")),cursor.getString(cursor.getColumnIndex(
                                    "startTime")),cursor.getString(cursor.getColumnIndex(
                                    "endTime")),cursor.getString(cursor.getColumnIndex(
                                    "universityId")),cursor.getString(cursor.getColumnIndex(
                                    "classType")),cursor.getString(cursor.getColumnIndex(
                                    "location")),cursor.getString(cursor.getColumnIndex(
                                    "timetable.id"))
                                    );
                            dataHolder.add(timetableObject);
                        }
                    }
                }
                recyclerView.setAdapter(new RecyclerViewAdapter(dataHolder));

            }
        });
        return view;
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



    public void replaceFragment(Fragment fragment)
    {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);

        fragmentTransaction.commit();
    }
}