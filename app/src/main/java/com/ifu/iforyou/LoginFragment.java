package com.ifu.iforyou;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginFragment extends Fragment {

    private Button btnRegister;
    private View view;
    private RegisterFragment registerFragment;
    private FragmentTransaction fragmentTransaction;
    private TextView txtForgotPassword;
    private ForgotPasswordFragment forgotPasswordFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        return view;
    }

    public void replaceFragment(Fragment fragment)
    {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.homeFrameLayout,fragment);
        fragmentTransaction.commit();
    }
}