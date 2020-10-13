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
import androidx.navigation.fragment.NavHostFragment;
import com.stepapp.step.databinding.FragmentLoginBinding;
import com.stepapp.step.interfaces.ILoginFragment;
import com.stepapp.step.repos.MainRepository;
import com.stepapp.step.utils.Constants;
import com.stepapp.step.viewmodels.LoginViewModel;

public class LoginFragment extends Fragment implements ILoginFragment {

    public String TAG = getClass().getName();
    private FragmentLoginBinding mBinding;
    private MainRepository repo;
    private LoginViewModel viewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(this.TAG, "onCreateView...");
        this.mBinding = FragmentLoginBinding.inflate(inflater);
        this.repo = MainRepository.getInstance();
        LoginViewModel loginViewModel = new LoginViewModel();
        this.viewModel = loginViewModel;
        this.mBinding.setData(loginViewModel);
        this.mBinding.setParent(this);
        return this.mBinding.getRoot();
    }


    public void goToMain() {
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }

    /* access modifiers changed from: package-private */
    public void alert(String title, String message) {
        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).setTitle(title).setMessage(message).setPositiveButton("OK", (DialogInterface.OnClickListener) null).setCancelable(true).show();
    }

    /* access modifiers changed from: package-private */
    public boolean fieldsValid() {
        String email = this.viewModel.getEmail();
        String password = this.viewModel.getPassword();
        if (email.isEmpty() || password.isEmpty()) {
            return false;
        }
        return true;
    }

    public void onLoginClick() {
        Log.v(this.TAG, "onLoginClick...");
        if (fieldsValid()) {
            getActivity().registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Log.v(TAG, "Broadcast receiver. onReceive...");
                    boolean isLoginSuccess = intent.getBooleanExtra(Constants.IS_SUCCESSFUL, false);
                    context.unregisterReceiver(this);
                    if (isLoginSuccess) {
                        goToMain();
                    } else {
                        alert("Error", intent.getStringExtra(Constants.MESSAGE).isEmpty() ? "Login failed" : intent.getStringExtra(Constants.MESSAGE));
                    }
                }
            }, new IntentFilter(Constants.INTENT_LOGIN_ACTION));
            MainRepository.getInstance().loginUser(this.viewModel.getEmail(), this.viewModel.getPassword(), getContext());
            return;
        }
        alert("Invalid", "Please input both email and password");
    }

    public void onForgotPasswordClick() {
        Log.v(this.TAG, "onForgotPasswordClick...");
        String email = this.viewModel.getEmail();
        if (email.isEmpty()) {
            new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).setTitle("Missing email").setMessage("Please provide your email in the email field").setCancelable(true).setPositiveButton("OK", (DialogInterface.OnClickListener) null).show();
            return;
        }
        getActivity().registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).setTitle("Reset Password").setMessage(intent.getStringExtra(Constants.MESSAGE)).setCancelable(true).setPositiveButton("OK", (DialogInterface.OnClickListener) null).show();
                context.unregisterReceiver(this);
            }
        }, new IntentFilter(Constants.INTENT_RESET_PASSWORD_ACTION));
        this.repo.forgotPassword(email, getContext());
    }

    public void onRegisterClick() {
        NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_registerFragment);
    }
}
