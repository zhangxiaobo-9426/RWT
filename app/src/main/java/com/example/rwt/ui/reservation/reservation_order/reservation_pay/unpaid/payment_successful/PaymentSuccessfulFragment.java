package com.example.rwt.ui.reservation.reservation_order.reservation_pay.unpaid.payment_successful;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rwt.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentSuccessfulFragment extends Fragment {


    public PaymentSuccessfulFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_successful, container, false);
    }

}
