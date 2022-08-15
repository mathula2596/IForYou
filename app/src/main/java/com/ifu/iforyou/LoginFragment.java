package com.ifu.iforyou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.security.GeneralSecurityException;

public class LoginFragment extends Fragment {

    private Button btnRegister, btnLogin;
    private View view;
    private RegisterFragment registerFragment;
    private FragmentTransaction fragmentTransaction;
    private TextView txtForgotPassword;
    private ForgotPasswordFragment forgotPasswordFragment;
    private TextInputLayout username, password;
    private String userRole;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";

    String loginName, loginRole = null;

    private DatabaseAccess databaseAccess;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        try {
            databaseAccess.adminAccountCreation();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        databaseAccess.close();

        view = inflater.inflate(R.layout.fragment_login, container, false);

        btnRegister = view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view1 -> {
            registerFragment = new RegisterFragment();
            replaceFragment(registerFragment);
        });

        txtForgotPassword = view.findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(view1 -> {
            forgotPasswordFragment = new ForgotPasswordFragment();
            replaceFragment(forgotPasswordFragment);

        });


        sharedPreferences = getActivity().getSharedPreferences(SHARED_PRE_NAME, Context.MODE_PRIVATE);
        loginName = sharedPreferences.getString(KEY_NAME,null);
        loginRole = sharedPreferences.getString(KEY_ROLE,null);

        if(loginName != null || loginRole != null)
        {
            selectDashboard(loginRole);
        }

        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view1 -> {
            username = view.findViewById(R.id.username);
            password = view.findViewById(R.id.password);
            registerFragment = new RegisterFragment();
            if(registerFragment.checkFieldEmpty(username) && registerFragment.checkFieldEmpty(password))
            {
                //DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
                databaseAccess.open();
                String result = "false";
                try {
                    result = databaseAccess.login(username.getEditText().getText().toString(),
                            password.getEditText().getText().toString());

                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(KEY_NAME,username.getEditText().getText().toString());
                    editor.putString(KEY_ROLE,result);
                    editor.apply();

                    selectDashboard(result);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                databaseAccess.close();
            }
        });

        return view;
    }

    public void replaceFragment(Fragment fragment)
    {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.homeFrameLayout,fragment);
        fragmentTransaction.commit();
    }

    public void selectDashboard(String result)
    {
        if(result.equals("Lecturer"))
        {
            startActivity(new Intent(getActivity(),LecturerDashboard.class));
            getActivity().finish();
        }
        else if(result.equals("Admin"))
        {
            startActivity(new Intent(getActivity(),AdminDashboard.class));
            getActivity().finish();
        }
        else{
            Toast.makeText(getActivity(),"Username or Password is wrong or you don't allowed to " +
                            "login!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}