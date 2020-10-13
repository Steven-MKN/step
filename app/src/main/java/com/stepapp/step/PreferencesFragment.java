package com.stepapp.step;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.annotations.NotNull;
import com.stepapp.step.databinding.FragmentPreferencesBinding;
import com.stepapp.step.databinding.FragmentSavedBinding;
import com.stepapp.step.firebase.Pref;
import com.stepapp.step.interfaces.IMapFragment;
import com.stepapp.step.interfaces.IPreferencesFragment;
import com.stepapp.step.repos.MainRepository;
import com.stepapp.step.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class PreferencesFragment extends Fragment implements IPreferencesFragment {
    private String TAG = getClass().getName();
    private FragmentPreferencesBinding mBinding;
    private MainRepository repo;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(this.TAG, "onCreateView...");
        FragmentPreferencesBinding inflate = FragmentPreferencesBinding.inflate(inflater);

        repo = MainRepository.getInstance();

        mBinding = inflate;

        mBinding.setParent(this);

        setupSpinners();

        getSavedSettings();

        return inflate.getRoot();
    }

    void getSavedSettings(){

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(TAG, "onReceive");

                if (intent.getBooleanExtra(Constants.IS_SUCCESSFUL, false)){

                    try {
                        Pref pref = (Pref) intent.getSerializableExtra(Pref.class.getName());

                        mBinding.editTextName.setText(pref.getName());

                        mBinding.spinnerUnitsOfMeasure.setSelection(
                                pref.getUnits().equals("Metric") ? 0 : 1,
                                true);

                        mBinding.spinnerLandmarkType.setSelection(
                                pref.getFavLandmark().equals("Modern") ? 0 : 1,
                                true);

                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }

                } else {
                    Log.v(TAG, "so settings found");
                }

                context.unregisterReceiver(this);
            }
        };

        getContext().registerReceiver(receiver, new IntentFilter(Constants.INTENT_GET_SETTINGS_ACTION));

        repo.getPreferences(getContext());
    }

    void setupSpinners(){
        ArrayList<String> landmarkTypes = new ArrayList<>();
        landmarkTypes.add("Modern");
        landmarkTypes.add("Historic");

        ArrayAdapter landmarkAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, landmarkTypes);
        Spinner spLandmarks = mBinding.spinnerLandmarkType;
        spLandmarks.setAdapter(landmarkAdapter);

        ArrayList<String> unitsTypes = new ArrayList<>();
        unitsTypes.add("Metric");
        unitsTypes.add("Imperial");

        ArrayAdapter spUintsAdaapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, unitsTypes);
        Spinner spUnits = mBinding.spinnerUnitsOfMeasure;
        spUnits.setAdapter(spUintsAdaapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        Pref pref = new Pref();
        pref.setName(mBinding.editTextName.getText().toString());
        pref.setFavLandmark(mBinding.spinnerLandmarkType.getSelectedItem().toString());
        pref.setUnits(mBinding.spinnerUnitsOfMeasure.getSelectedItem().toString());

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(TAG, "onReceive");
                context.unregisterReceiver(this);
            }
        };

        getContext().registerReceiver(receiver, new IntentFilter(Constants.INTENT_SAVE_SETTINGS_ACTION));

        repo.savePreferences(pref, getContext());
    }

    @Override
    public void onResetPassword() {
        Log.v(this.TAG, "onResetPasswordClicked...");
        getContext().registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.v(TAG, "onReceive...");
                if (intent.getBooleanExtra(Constants.IS_SUCCESSFUL, false)) {
                    alert("Successful", "A reset link was sent to your email");
                } else {
                    alert("Failed", intent.getStringExtra(Constants.MESSAGE));
                }
                context.unregisterReceiver(this);
            }
        }, new IntentFilter(Constants.INTENT_RESET_PASSWORD_ACTION));
        this.repo.forgotPassword((String) null, getContext());
    }

    @Override
    public void onLogout() {
        Log.v(this.TAG, "onLogoutClicked...");
        getContext().registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.v(TAG, "onReceive...");
                context.unregisterReceiver(this);
                restartApp();
            }
        }, new IntentFilter(Constants.INTENT_SIGN_OUT_ACTION));
        this.repo.logout(getContext());
    }

    @Override
    public void onAbout() {

    }

    @Override
    public void onDeleteData() {

    }

    public void alert(String title, String message) {
        Log.v(this.TAG, "alert...");
        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).setTitle(title).setMessage(message).setPositiveButton("OK", (DialogInterface.OnClickListener) null).setCancelable(true).show();
    }

    public void restartApp() {
        Log.v(this.TAG, "restartApp...");
        startActivity(new Intent(getContext(), StartActivity.class));
        getActivity().finish();
    }
}
