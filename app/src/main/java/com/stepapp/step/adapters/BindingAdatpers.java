package com.stepapp.step.adapters;

import android.util.Log;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BindingAdatpers {
    private static String S_TAG = "com.stepapp.step.adapters.BindingAdapters";

    @BindingAdapter(value = {"app:places"}, requireAll = true)
    public static void setMealList(RecyclerView view, ArrayList<String> places) {
        Log.v(S_TAG, "setplacesList...");
        if (places != null) {
            if (view.getLayoutManager() == null) {
                Log.i(S_TAG, "layout manager is null, creating...");
                view.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
            }
            PlaceAdapter timelineAdapter = (PlaceAdapter) view.getAdapter();
            if (timelineAdapter == null) {
                Log.i(S_TAG, "Product Adapter is null, creating...");
                view.setAdapter(new PlaceAdapter(places));
                return;
            }
            timelineAdapter.updateList(places);
            timelineAdapter.notifyDataSetChanged();
        }
    }
}
