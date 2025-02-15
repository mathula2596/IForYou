package com.ifu.iforyou;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ResetPasswordFragment extends Fragment {
    private View view;

    private Cursor cursor = null;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";
    String loginName, loginRole = null;

    private Boolean validPassword, validField = false;

    private TextInputLayout txtCurrentPassword, txtPassword, txtConfirmPassword;
    private Button btnReset;
    private DatabaseAccess databaseAccess;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_reset_password, container, false);

        sharedPreferences = getActivity().getSharedPreferences(SHARED_PRE_NAME,MODE_PRIVATE);
        loginName = sharedPreferences.getString(KEY_NAME,null);
        loginRole = sharedPreferences.getString(KEY_ROLE,null);

        txtCurrentPassword = view.findViewById(R.id.oldPassword);
        txtPassword = view.findViewById(R.id.newPassword);
        txtConfirmPassword = view.findViewById(R.id.confirmPassword);

        btnReset = view.findViewById(R.id.btnReset);

        btnReset.setOnClickListener(v -> {
            if(isValidPassword(txtPassword) && isValidPassword(txtConfirmPassword))
            {
                if(txtPassword.getEditText().getText().toString().equals(txtConfirmPassword.getEditText().getText().toString()))
                {
                    txtConfirmPassword.setError(null);
                    databaseAccess = DatabaseAccess.getInstance(getActivity());
                    databaseAccess.open();
                    boolean result = false;
                    try {
                        result = databaseAccess.resetPassword(loginName,
                                txtPassword.getEditText().getText().toString(),
                                txtCurrentPassword.getEditText().getText().toString());
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }

                    if(result)
                    {
                        Toast.makeText(getActivity(),"Password Reset Successfully!",
                                Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(getContext(),MainActivity.class));
                        getActivity().finish();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Current password is wrong or Try again " +
                                        "later!!!",
                                Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    txtConfirmPassword.setError("Passwords do not match!!!");
                }
            }
        });

        return view;
    }
    public boolean isValidPassword(TextInputLayout textField) {
        validPassword = false;
        String password = textField.getEditText().getText().toString();
        Pattern pattern;
        Matcher matcher;
        final String PasswordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PasswordPattern);
        matcher = pattern.matcher(password);

        if(matcher.matches()){
            textField.setError(null);
            validPassword = true;

        }else{
            textField.setError("Password should be more than 8 characters and contain Uppercase, Symbol and Number");
            validPassword = false;

        }
        return validPassword;
    }
}