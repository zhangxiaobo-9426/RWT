package com.example.rwt.ui.reservation.reservation_order;


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
public class ReservationOrderFragment extends BaseFragment {

    private Button button_reservation_now,button_cancel_appointment;

    public ReservationOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button_reservation_now = view.findViewById(R.id.button_reservation_now);
        button_reservation_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_reservationOrderFragment_to_orderFragment);
            }
        });
    }
}
