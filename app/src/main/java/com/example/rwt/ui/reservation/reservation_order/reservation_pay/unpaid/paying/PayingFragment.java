package com.example.rwt.ui.reservation.reservation_order.reservation_pay.unpaid.paying;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.rwt.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PayingFragment extends Fragment {
    private ImageView paying_ImageView;

    public PayingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_paying, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        paying_ImageView = view.findViewById(R.id.paying_ImageView);
        paying_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_payingFragment_to_paymentSuccessfulFragment);
            }
        });
    }
}
