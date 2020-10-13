package com.stepapp.step;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.stepapp.step.databinding.FragmentMapBinding;
import com.stepapp.step.databinding.FragmentSavedBinding;
import com.stepapp.step.interfaces.IMapFragment;
import com.stepapp.step.interfaces.ISavedFragment;

public class SavedFragment extends Fragment implements ISavedFragment {
    private String TAG = getClass().getName();
    private FragmentSavedBinding mBinding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(this.TAG, "onCreateView...");
        FragmentSavedBinding inflate = FragmentSavedBinding.inflate(inflater);
        this.mBinding = inflate;
        return inflate.getRoot();
    }

}
