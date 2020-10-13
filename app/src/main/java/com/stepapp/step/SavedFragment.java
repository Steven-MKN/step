package com.stepapp.step;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.stepapp.step.databinding.FragmentLoginBinding;
import com.stepapp.step.databinding.FragmentMapBinding;
import com.stepapp.step.databinding.FragmentSavedBinding;
import com.stepapp.step.interfaces.IMapFragment;
import com.stepapp.step.interfaces.ISavedFragment;
import com.stepapp.step.repos.MainRepository;
import com.stepapp.step.utils.Constants;
import com.stepapp.step.utils.MyPlaces;
import com.stepapp.step.viewmodels.LoginViewModel;
import com.stepapp.step.viewmodels.SavedViewModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment implements ISavedFragment {
    private String TAG = getClass().getName();
    private FragmentSavedBinding mBinding;
    private MainRepository repo;
    private SavedViewModel savedViewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(this.TAG, "onCreateView...");

        mBinding = FragmentSavedBinding.inflate(inflater);
        repo = MainRepository.getInstance();
        savedViewModel = new SavedViewModel();

        getUpdatedData();

        mBinding.setData(savedViewModel);

        return mBinding.getRoot();
    }

    void getUpdatedData(){
        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra(Constants.IS_SUCCESSFUL, false)){
                    ArrayList<String> places = (ArrayList<String>) intent.getStringArrayListExtra("places");
                    if (places == null){
                        Log.d(TAG, "places array list is null");
                    } else {
                        Log.d(TAG, places.toString());

                        // get place details
                        //PlacesClient placesClient = Places.createClient(getContext());
                        ArrayList<MyPlaces> myPlaces = new ArrayList<>();

//                        List<Place.Field> fieldList = new ArrayList<>();
//                        fieldList.add(Place.Field.ADDRESS);
//                        fieldList.add(Place.Field.NAME);

                        for (Object objId : places.toArray()) {
//                            FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(places.get(0), fieldList).build();
//                            placesClient.fetchPlace(fetchPlaceRequest).;
                            String id = (String) objId;
                            myPlaces.add(new MyPlaces(id, "", ""));
                        }

                        savedViewModel.setPlaces(myPlaces);
                    }
                } else {
                    Log.d(TAG, "unsuccessful in getting places");
                }
            }
        }, new IntentFilter(Constants.INTENT_GET_PLACES_ACTION));

        repo.getPlaces(getContext());
    }

}
