package com.example.rwt.ui.reservation.reservation_order.reservation_pay.unpaid.payment_successful;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.rwt.R;
import com.example.rwt.ui.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentSuccessfulFragment extends BaseFragment {


     private Button button_payment_successful_navigation;
    public PaymentSuccessfulFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_successful, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button_payment_successful_navigation = view.findViewById(R.id.button_payment_successful_navigation);
        button_payment_successful_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_paymentSuccessfulFragment_to_navigationFragment);
            }
        });
    }
}
