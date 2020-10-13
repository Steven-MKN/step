package com.stepapp.step.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class LoginViewModel extends BaseObservable {
    private String email = "";
    private String password = "";

    @Bindable
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email2) {
        if (this.email != email2) {
            this.email = email2;
            notifyPropertyChanged(6);
        }
    }

    @Bindable
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password2) {
        if (this.password != password2) {
            this.password = password2;
            notifyPropertyChanged(16);
        }
    }
}
