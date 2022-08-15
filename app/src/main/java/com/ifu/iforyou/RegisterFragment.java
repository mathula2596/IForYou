package com.ifu.iforyou;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.StrictMode;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class RegisterFragment extends Fragment {

    private Button btnLogin, btnRegister;
    private View view;
    private FragmentTransaction fragmentTransaction;
    private LoginFragment loginFragment;
    private RadioButton rdbStudent, rdbLecturer;
    private TextInputLayout universityId, firstName, lastName, email, password, confirmPassword,
    courseBorder,batchNumberBorder,degreeLevelBorder;
    private AutoCompleteTextView course, degreeLevel, batchNumber;
    private String [] courseName = {"Computing Systems", "Computer Science", "Data Science",
            "Cyber Security", "Engineering"};
    private String [] degree = {"Undergraduate","Masters","PhD"};
    private String [] batch = {"DS2021Jan","DS2021Sep","Comp2021Jan","Comp2021Sep","Comp2022Jan",
            "Comp2022Sep"};
    private ArrayAdapter<String> arrayAdapter;
    private String status = "Active";
    private String userRole = "Student";
    private boolean validPassword, validUniversityID,validDropdown, validEmail, validField = false;
    final String username = "mathula2011@gmail.com";
    final String emailPassword = "jmwwdizaqbgabsse";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_register, container, false);

        course = view.findViewById(R.id.course);
        arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,courseName);
        course.setAdapter(arrayAdapter);

        degreeLevel = view.findViewById(R.id.degreeLevel);
        arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,degree);
        degreeLevel.setAdapter(arrayAdapter);

        batchNumber = view.findViewById(R.id.batchNumber);
        arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,batch);
        batchNumber.setAdapter(arrayAdapter);

        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view1 -> {
            loginFragment = new LoginFragment();
           replaceFragment(loginFragment);
        });

        universityId = view.findViewById(R.id.universityId);
        firstName = view.findViewById(R.id.firstname);
        lastName = view.findViewById(R.id.lastname);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        rdbLecturer = view.findViewById(R.id.rdbLecturer);
        rdbStudent = view.findViewById(R.id.rdbStudent);

        if(rdbStudent.isChecked())
        {
            userRole = "Student";
        }
        else if(rdbLecturer.isChecked())
        {
            userRole = "Lecturer";
        }
        courseBorder = view.findViewById(R.id.courseBorder);
        degreeLevelBorder = view.findViewById(R.id.degreeLevelBorder);
        batchNumberBorder = view.findViewById(R.id.batchNumberBorder);

        rdbLecturer.setOnClickListener(view1 -> {
            if(rdbLecturer.isChecked())
            {
                course.setVisibility(View.GONE);
                courseBorder.setVisibility(View.GONE);

                batchNumber.setVisibility(View.GONE);
                batchNumberBorder.setVisibility(View.GONE);

                degreeLevel.setVisibility(View.GONE);
                degreeLevelBorder.setVisibility(View.GONE);

                userRole = "Lecturer";
            }
            else if(rdbStudent.isChecked())
            {
                course.setVisibility(View.VISIBLE);
                courseBorder.setVisibility(View.VISIBLE);

                batchNumber.setVisibility(View.VISIBLE);
                batchNumberBorder.setVisibility(View.VISIBLE);

                degreeLevel.setVisibility(View.VISIBLE);
                degreeLevelBorder.setVisibility(View.VISIBLE);

                userRole = "Student";
            }
        });

        rdbStudent.setOnClickListener(view1 -> {
            if(rdbStudent.isChecked())
            {
                course.setVisibility(View.VISIBLE);
                courseBorder.setVisibility(View.VISIBLE);

                batchNumber.setVisibility(View.VISIBLE);
                batchNumberBorder.setVisibility(View.VISIBLE);

                degreeLevel.setVisibility(View.VISIBLE);
                degreeLevelBorder.setVisibility(View.VISIBLE);

                userRole = "Student";
            }
            else if(rdbLecturer.isChecked())
            {
                course.setVisibility(View.GONE);
                courseBorder.setVisibility(View.GONE);

                batchNumber.setVisibility(View.GONE);
                batchNumberBorder.setVisibility(View.GONE);

                degreeLevel.setVisibility(View.GONE);
                degreeLevelBorder.setVisibility(View.GONE);

                userRole = "Lecturer";
            }
        });

        btnRegister = view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view1 -> {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
            databaseAccess.open();
            if(password.getEditText().getText().toString().equals(confirmPassword.getEditText().getText().toString()))
            {
                if(isValidUniveristyID(universityId) && checkFieldEmpty(firstName)&&checkFieldEmpty(lastName) && emailValidator(email) && checkDropDownEmpty(course)&&checkDropDownEmpty(degreeLevel)&&checkDropDownEmpty(batchNumber)&&isValidPassword(password))
                {
                    boolean result = false;

                    try {
                        result = databaseAccess.register(universityId.getEditText().getText().toString(),
                        firstName.getEditText().getText().toString(),
                        lastName.getEditText().getText().toString(),email.getEditText().getText().toString(),
                        password.getEditText().getText().toString(),
                        userRole,
                        course.getText().toString(),degreeLevel.getText().toString(),batchNumber.getText().toString(),status);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }

                    if(result)
                    {
                        Toast.makeText(getActivity(),"Registration Success!",Toast.LENGTH_SHORT).show();

                        Properties props = new Properties();
                        props.put("mail.smtp.auth","true");
                        props.put("mail.smtp.starttls.enable","true");
                        props.put("mail.smtp.host","smtp.gmail.com");
                        props.put("mail.smtp.port","587");

                        Session session = Session.getInstance(props, new javax.mail.Authenticator(){
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, emailPassword);
                            }
                        });
                        try{
                            Message message = new MimeMessage(session);
                            message.setFrom(new InternetAddress(username));
                            message.setRecipients(Message.RecipientType.TO,
                                    InternetAddress.parse(email.getEditText().getText().toString()));
                            message.setSubject("Account Details");
                            message.setText("Username - " + universityId.getEditText().getText() + " \n" + "Password - " + password.getEditText().getText().toString());
                            StrictMode.ThreadPolicy policy =
                                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            Transport.send(message);

                        }
                        catch (MessagingException e)
                        {
                            throw new RuntimeException(e);
                        }
                        loginFragment = new LoginFragment();
                        replaceFragment(loginFragment);

                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Registration Failed. You may already " +
                                "registered!",Toast.LENGTH_SHORT).show();
                    }

                    databaseAccess.close();
                }

            }
            else
            {
                confirmPassword.setError("Passwords do not match!");
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

    public boolean isValidUniveristyID(TextInputLayout textField) {
        validUniversityID = false;
        String password = textField.getEditText().getText().toString();
        Pattern pattern;
        Matcher matcher;
        final String PasswordPattern = "(^[a-zA-Z][0-9]{7}$)";
        pattern = Pattern.compile(PasswordPattern);
        matcher = pattern.matcher(password);

        if(matcher.matches()){
            textField.setError(null);
            validUniversityID = true;

        }else{
            textField.setError("University ID must be in correct format");
            validUniversityID = false;

        }
        return validUniversityID;
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
    public boolean emailValidator(TextInputLayout textField){
        validEmail = false;
        String email = textField.getEditText().getText().toString();
        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            textField.setError(null);
            validEmail = true;
        }
        else
        {
            textField.setError("Please fill the correct value");
            validEmail = false;
        }
        return validEmail;
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
}