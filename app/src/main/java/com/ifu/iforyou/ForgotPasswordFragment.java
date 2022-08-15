package com.ifu.iforyou;

import android.content.ActivityNotFoundException;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class ForgotPasswordFragment extends Fragment {

    private Button btnLogin, btnSend;
    private View view;
    private FragmentTransaction fragmentTransaction;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private TextInputLayout email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view1 -> {
            loginFragment = new LoginFragment();
            replaceFragment(loginFragment);
        });

        btnSend = view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(view1 -> {
            registerFragment = new RegisterFragment();
            email = view.findViewById(R.id.email);
            if(registerFragment.emailValidator(email))
            {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
                databaseAccess.open();
                boolean result = false;
                result =
                        databaseAccess.forgotPasswordEmailCheck(email.getEditText().getText().toString());

                if(result)
                {
                    if(databaseAccess.sendPassword(email.getEditText().getText().toString()))
                    {
                         Toast.makeText(getActivity(), "Please check your mail for the temporary password",
                               Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Failed to reset! Try again", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "This email is not registered",
                            Toast.LENGTH_SHORT).show();
                }

                databaseAccess.close();
                loginFragment = new LoginFragment();
                replaceFragment(loginFragment);

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
}