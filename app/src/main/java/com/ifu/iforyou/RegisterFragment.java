package com.ifu.iforyou;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class RegisterFragment extends Fragment {

    private Button btnLogin;
    private View view;
    private FragmentTransaction fragmentTransaction;
    private LoginFragment loginFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_register, container, false);

        btnLogin = view.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view1 -> {
            loginFragment = new LoginFragment();
           replaceFragment(loginFragment);
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