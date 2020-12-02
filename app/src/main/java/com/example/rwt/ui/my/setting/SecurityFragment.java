package com.example.rwt.ui.my.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rwt.R;
import com.example.rwt.databinding.FragmentSecurityBinding;

public class SecurityFragment extends Fragment {
    FragmentSecurityBinding binding ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_security,container,false);
        return binding.getRoot();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.mySecurityBack.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
//      binding.mySecurityRealName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }

}
