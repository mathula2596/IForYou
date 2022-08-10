package com.ifu.iforyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudentDashboard extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";


    TextView txtTitle;
    Button btnLogout;

    String loginName, loginRole = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        sharedPreferences = getSharedPreferences(SHARED_PRE_NAME,MODE_PRIVATE);
        loginName = sharedPreferences.getString(KEY_NAME,null);
        loginRole = sharedPreferences.getString(KEY_ROLE,null);

        txtTitle = findViewById(R.id.txtTitle);
        btnLogout = findViewById(R.id.btnLogout);

        if(loginName != null)
        {
            txtTitle.setText(loginName);
        }
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            }
        });
    }
}