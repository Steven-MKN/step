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

import androidx.fragment.app.Fragment;
import com.stepapp.step.databinding.FragmentRegisterBinding;
import com.stepapp.step.interfaces.IRegisterFragment;
import com.stepapp.step.repos.MainRepository;
import com.stepapp.step.utils.Constants;
import com.stepapp.step.viewmodels.RegisterViewModel;

public class RegisterFragment extends Fragment implements IRegisterFragment {
    public String TAG = getClass().getName();
    private FragmentRegisterBinding mBinding;
    private MainRepository repo;
    private RegisterViewModel viewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(this.TAG, "onCreateView...");
        this.mBinding = FragmentRegisterBinding.inflate(inflater);
        this.repo = MainRepository.getInstance();
        RegisterViewModel registerViewModel = new RegisterViewModel();
        this.viewModel = registerViewModel;
        this.mBinding.setData(registerViewModel);
        this.mBinding.setParent(this);
        return this.mBinding.getRoot();
    }

    public void onLoginClick() {
        getActivity().onBackPressed();
    }

    public void onCreateAccountClick() {
        int fieldErrors = validateFields();
        if (fieldErrors == 0) {
            getContext().registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String title;
                    Log.v(TAG, "Broadcast receiver. onReceive...");
                    String message = intent.getStringExtra(Constants.MESSAGE);
                    if (message == null || message.isEmpty()) {
                        title = "Success";
                        message = "Hey, account created successfully. You can login now";
                    } else {
                        title = "error";
                    }
                    new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).setCancelable(true).setTitle(title).setMessage(message).setPositiveButton("OK", (DialogInterface.OnClickListener) null).show();
                    context.unregisterReceiver(this);
                }
            }, new IntentFilter(Constants.INTENT_REGISTER_ACTION));
            repo.signUpUser(this.viewModel.getEmail(), this.viewModel.getPassword(), getContext());
            return;
        }
        String title = "";
        String message = "";
        if (fieldErrors == 1) {
            title = "Invalid email";
            message = "Please provide an email";
        } else if (fieldErrors == 2) {
            title = "Password mismatch";
            message = "The passwords you entered do not match. Please enter a valid password";
        }
        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).setCancelable(true).setTitle(title).setMessage(message).setPositiveButton("OK", (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: package-private */
    public int validateFields() {
        int fieldErrors = 0;
        if (this.viewModel.getEmail().isEmpty()) {
            fieldErrors = 1;
        }
        if (this.viewModel.getPassword().isEmpty() || !this.viewModel.getPassword().equals(this.viewModel.getConfirmPassword())) {
            return 2;
        }
        return fieldErrors;
    }
}
