package com.ifu.iforyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class AdminDashboard extends AppCompatActivity {

    MaterialToolbar appbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";
    String loginName, loginRole = null;
    private View navHeader;
    public TextView username;
    private FragmentTransaction fragmentTransaction;
    private RegisterFragment registerFragment;
    private TimetableFragment timetableFragment;
    private ViewTimetableFragment viewTimetableFragment;
    private ResetPasswordFragment resetPasswordFragment;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sharedPreferences = getSharedPreferences(SHARED_PRE_NAME,MODE_PRIVATE);
        loginName = sharedPreferences.getString(KEY_NAME,null);
        loginRole = sharedPreferences.getString(KEY_ROLE,null);

        if(loginName != null)
        {
            //txtTitle.setText(loginName);
        }

        appbar= findViewById(R.id.appbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        navHeader = navigationView.findViewById(R.layout.nav_header);

        appbar.setNavigationOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
            username = navigationView.findViewById(R.id.username);
            username.setText(loginName);
        });

        viewTimetableFragment = new ViewTimetableFragment();
        replaceFragment(viewTimetableFragment);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id)
                {

                    case R.id.nav_logout:
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        break;
                    case R.id.nav_register:
                        registerFragment = new RegisterFragment();
                        replaceFragment(registerFragment);
                        break;
                    case R.id.nav_timetable:
                        timetableFragment = new TimetableFragment();
                        replaceFragment(timetableFragment);
                        break;
                    case R.id.nav_view_timetable:
                        viewTimetableFragment = new ViewTimetableFragment();
                        replaceFragment(viewTimetableFragment);
                        break;
                    case R.id.nav_home:
                        viewTimetableFragment = new ViewTimetableFragment();
                        replaceFragment(viewTimetableFragment);
                        break;
                    case R.id.nav_change_password:
                        resetPasswordFragment = new ResetPasswordFragment();
                        replaceFragment(resetPasswordFragment);
                        break;
                    default:
                        return true;

                }
                return true;
            }
        });
    }
    public void replaceFragment(Fragment fragment)
    {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment).addToBackStack("back");
        fragmentTransaction.commit();
    }
}