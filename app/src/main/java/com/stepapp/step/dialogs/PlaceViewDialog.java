package com.stepapp.step.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.stepapp.step.R;

public class PlaceViewDialog extends Fragment {
    private String title, address, id;
    private LatLng latLng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_place_view, null);

        Bundle bundle = getArguments();

        if (bundle != null) {
            this.title = bundle.getString("title");
            this.address = bundle.getString("address");
            this.id = bundle.getString("id");

            double lat = bundle.getDouble("lat");
            double lng = bundle.getDouble("lng");

            this.latLng = new LatLng(lat, lng);
        }

        ((TextView) view.findViewById(R.id.text_view_place_title)).setText(title);
        ((TextView) view.findViewById(R.id.text_view_place_address)).setText(address);

        ((Button) view.findViewById(R.id.button_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((Button) view.findViewById(R.id.button_navigate_to)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return  view;
    }
}
