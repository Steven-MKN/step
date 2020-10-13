package com.stepapp.step.repos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepapp.step.firebase.Pref;
import com.stepapp.step.utils.Constants;
import com.stepapp.step.utils.Utils;

import java.util.ArrayList;

public class MainRepository {
    private static MainRepository instance;
    public String TAG = getClass().getName();
    public FirebaseUser currentUser;
    public DatabaseReference database;
    public FirebaseAuth mAuth;
    public Utils.MEASURING_SYSTEM measuringSystem;
    public SharedPreferences sharedPreferences;

    public static class SP {
        public static final String MEASURING_SYSTEM = "measuring_system";

        private SP() {
        }
    }

    private MainRepository() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseAuth instance2 = FirebaseAuth.getInstance();
        this.mAuth = instance2;
        this.currentUser = instance2.getCurrentUser();
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    public static MainRepository getInstance() {
        if (instance == null) {
            instance = new MainRepository();
        }
        return instance;
    }

    public void initRepo(Context ctx, SharedPreferences sp) {
        this.sharedPreferences = sp;
        this.measuringSystem = sp.getString(SP.MEASURING_SYSTEM, "m").equals("m") ? Utils.MEASURING_SYSTEM.METRIC : Utils.MEASURING_SYSTEM.IMPERIAL;
        Places.initialize(ctx, "AIzaSyBgROf_tJfYZKmfbftkFDpZDo7vUoEGuuo");

    }

    public FirebaseUser getCurrentUser() {
        Log.v(TAG, "getCurrentUser...");
        return currentUser;
    }

    public Utils.MEASURING_SYSTEM getMeasuringSystem() {
        Utils.MEASURING_SYSTEM measuring_system = measuringSystem;
        if (measuring_system == null) {
            return Utils.MEASURING_SYSTEM.METRIC;
        }
        return measuring_system;
    }

    public void logout(final Context context) {
        Log.v(TAG, "logout...");
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    FirebaseUser unused = currentUser = null;
                    context.sendBroadcast(new Intent(Constants.INTENT_SIGN_OUT_ACTION));
                }
            }
        });
        mAuth.signOut();
    }

    public void signUpUser(String email, String password, final Context context) {
        Log.v(TAG, "signUpUser...");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    MainRepository mainRepository = MainRepository.this;
                    FirebaseUser unused = mainRepository.currentUser = mainRepository.mAuth.getCurrentUser();
                    context.sendBroadcast(new Intent(Constants.INTENT_REGISTER_ACTION));
                    return;
                }

                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                Log.i(TAG, "runnable running" + task.getException().getMessage());

                Intent intent = new Intent(Constants.INTENT_REGISTER_ACTION);
                intent.putExtra(Constants.MESSAGE, task.getException().getMessage());
                context.sendBroadcast(intent);
            }
        });
    }

    public void loginUser(String email, String password, final Context context) {
        Log.v(TAG, "loginUser...");
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmailAndPassword:success");
                    MainRepository mainRepository = MainRepository.this;
                    FirebaseUser unused = mainRepository.currentUser = mainRepository.mAuth.getCurrentUser();
                    Intent intent = new Intent(Constants.INTENT_LOGIN_ACTION);
                    intent.putExtra(Constants.IS_SUCCESSFUL, true);
                    context.sendBroadcast(intent);
                    return;
                }
                Log.d(TAG, "signInWithEmailAndPassword:failure");
                Intent intent2 = new Intent(Constants.INTENT_LOGIN_ACTION);
                intent2.putExtra(Constants.IS_SUCCESSFUL, false);
                intent2.putExtra(Constants.MESSAGE, task.getException().getMessage());
                context.sendBroadcast(intent2);
            }
        });
    }

    public void forgotPassword(String email, final Context context) {
        if (email == null && currentUser == null) {
            Intent intent = new Intent(Constants.INTENT_RESET_PASSWORD_ACTION);
            intent.putExtra(Constants.IS_SUCCESSFUL, false);
            context.sendBroadcast(intent);
            return;
        }
        if (email == null) {
            email = currentUser.getEmail();
        }
        Log.v(TAG, "forgotPassword...");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "sendPasswordResetEmail:success");
                    Intent intent = new Intent(Constants.INTENT_RESET_PASSWORD_ACTION);
                    intent.putExtra(Constants.IS_SUCCESSFUL, true);
                    context.sendBroadcast(intent);
                    return;
                }
                Log.d(TAG, "sendPasswordResetEmail:failed");
                Intent intent2 = new Intent(Constants.INTENT_RESET_PASSWORD_ACTION);
                intent2.putExtra(Constants.IS_SUCCESSFUL, false);
                intent2.putExtra(Constants.MESSAGE, task.getException().getMessage());
                context.sendBroadcast(intent2);
            }
        });
    }

    public void savePreferences(Pref pref, final Context context){
        Log.v(TAG, "savePreferences...");
        database.child(currentUser.getUid()).child(Constants.SETTINGS)
                .setValue(pref)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(Constants.INTENT_GET_SETTINGS_ACTION);
                        if (task.isSuccessful()){
                            intent.putExtra(Constants.IS_SUCCESSFUL, true);
                        } else {
                            intent.putExtra(Constants.IS_SUCCESSFUL, false);
                        }
                        context.sendBroadcast(intent);
                    }
                });
    }

    public void getPreferences(final Context context){
        Log.v(TAG, "getPreferences...");
        database.child(currentUser.getUid()).child(Constants.SETTINGS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent intent = new Intent(Constants.INTENT_GET_SETTINGS_ACTION);
                        if (dataSnapshot.exists() && dataSnapshot.hasChildren()){
                            intent.putExtra(Constants.IS_SUCCESSFUL, true);
                            intent.putExtra(Pref.class.getName(), (Pref) dataSnapshot.getValue(Pref.class));
                        } else {
                            intent.putExtra(Constants.IS_SUCCESSFUL, false);
                        }

                        context.sendBroadcast(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Intent intent = new Intent(Constants.INTENT_GET_SETTINGS_ACTION);
                        intent.putExtra(Constants.IS_SUCCESSFUL, false);
                        context.sendBroadcast(intent);
                    }
                });
    }

    public void savePlace(String id, final Context context){
        Log.v(TAG, "savePlace...");
        database.child(currentUser.getUid()).child("places")
                .push()
                .setValue(id)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(Constants.INTENT_SAVE_PLACE_ACTION);
                        if (task.isSuccessful())
                            intent.putExtra(Constants.IS_SUCCESSFUL, true);
                        else intent.putExtra(Constants.IS_SUCCESSFUL, false);

                        context.sendBroadcast(intent);
                    }
                });
    }

    public void getPlaces(final Context context){
        Log.v(TAG, "getPlaces");
        database.child(currentUser.getUid()).child("places")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent intent = new Intent(Constants.INTENT_GET_PLACES_ACTION);
                        if (dataSnapshot.hasChildren()){
                            intent.putExtra(Constants.IS_SUCCESSFUL, true);

                            ArrayList<String> places = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                places.add((String)snapshot.getValue().toString());
                            }

                            intent.putStringArrayListExtra("places", places);

                        } else {
                            intent.putExtra(Constants.IS_SUCCESSFUL, false);
                        }
                        context.sendBroadcast(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
