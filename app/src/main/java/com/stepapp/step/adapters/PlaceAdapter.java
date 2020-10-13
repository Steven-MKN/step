package com.stepapp.step.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.stepapp.step.R;
import com.stepapp.step.databinding.WidgetPlacesPlaceBinding;
import com.stepapp.step.utils.MyPlaces;
import com.stepapp.step.viewmodels.WidgetPlacesViewModel;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.BindingHolder> {
    public static String S_TAG = "com.stepapp.step.adapters.PlaceAdapter";
    private String TAG;
    private ArrayList<MyPlaces> places;

    public PlaceAdapter(ArrayList<MyPlaces> places) {
        String name = getClass().getName();
        this.TAG = name;
        Log.v(name, "PlaceAdapter...");
        this.places = places;
    }

    public void updateList(ArrayList<MyPlaces> places) {
        this.places = places;
    }

    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v(this.TAG, "onCreateViewHolder...");
        return new BindingHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.widget_places_place, parent, false).getRoot());
    }

    public void onBindViewHolder(BindingHolder holder, int position) {
        Log.v(this.TAG, "onBindViewHolder...");

        holder.binding.setData(new WidgetPlacesViewModel(places.get(position)));
        holder.binding.executePendingBindings();
    }

    public int getItemCount() {
        Log.v(this.TAG, "getItemCount...");
        return this.places.size();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        WidgetPlacesPlaceBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
            Log.i(S_TAG, "BindingHolder creating instance...");
            this.binding = DataBindingUtil.bind(itemView);
        }
    }
}