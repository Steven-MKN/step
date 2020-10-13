package com.stepapp.step.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.stepapp.step.R;
import com.stepapp.step.repos.MainRepository;
import com.stepapp.step.utils.Constants;

public class PlaceViewDialog extends Fragment {
    private String title, address, id;
    private LatLng latLng;
    private MainRepository repo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_place_view, null);

        repo = MainRepository.getInstance();

        Bundle bundle = getArguments();

        if (bundle != null) {
            this.title = bundle.getString("title");
            this.address = bundle.getString("address");
            this.id = bundle.getString("id");

//            double lat = bundle.getDouble("lat");
//            double lng = bundle.getDouble("lng");
//
//            this.latLng = new LatLng(lat, lng);
        }

        ((TextView) view.findViewById(R.id.text_view_place_title)).setText(title);
        ((TextView) view.findViewById(R.id.text_view_place_address)).setText(address);

        ((Button) view.findViewById(R.id.button_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getBooleanExtra(Constants.IS_SUCCESSFUL, false)){
                            Toast.makeText(context, "Place saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Save error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new IntentFilter(Constants.INTENT_SAVE_PLACE_ACTION));

                repo.savePlace(id, getContext());
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
