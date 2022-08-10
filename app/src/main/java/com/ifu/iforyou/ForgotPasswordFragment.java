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
    final String username = "mathula2011@gmail.com";
    final String password = "jmwwdizaqbgabsse";
    private String randomPassword;
    private Random random;
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
                try {
                    result =
                            databaseAccess.forgotPasswordEmailCheck(email.getEditText().getText().toString());

                    if(result)
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
                            message.setRecipients(Message.RecipientType.TO,
                                    InternetAddress.parse(email.getEditText().getText().toString()));
                            message.setSubject("Reset Password");
                            random = new Random();
                            randomPassword = String.format("%04d", random.nextInt(10000));
                            boolean result2 =
                                    databaseAccess.forgotPasswordUpdatePassword(email.getEditText().getText().toString(),randomPassword);
                            if(result2){
                                message.setText("New Password - " + randomPassword);
                                StrictMode.ThreadPolicy policy =
                                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                Transport.send(message);
                                Toast.makeText(getActivity(), "Please check your mail for the temporary password",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getActivity(), "Failed to reset! Try again",
                                        Toast.LENGTH_SHORT).show();
                            }


                        }
                        catch (MessagingException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "This email is not registered",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
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