package com.example.pokedexapp.filters;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class FilterState implements Parcelable {
    public List<String> selectedTypes = new ArrayList<>();
    public List<String> selectedGenerations = new ArrayList<>();
    public double minWeightKg = 0.0, maxWeightKg = 999.0;
    public double minHeightM = 0.0, maxHeightM = 20.0;

    public FilterState() {}

    protected FilterState(Parcel in) {
        selectedTypes = in.createStringArrayList();
        selectedGenerations = in.createStringArrayList();
        minWeightKg = in.readDouble();
        maxWeightKg = in.readDouble();
        minHeightM = in.readDouble();
        maxHeightM = in.readDouble();
    }

    public static final Creator<FilterState> CREATOR = new Creator<FilterState>() {
        @Override public FilterState createFromParcel(Parcel in) { return new FilterState(in); }
        @Override public FilterState[] newArray(int size) { return new FilterState[size]; }
    };

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(selectedTypes);
        dest.writeStringList(selectedGenerations);
        dest.writeDouble(minWeightKg);
        dest.writeDouble(maxWeightKg);
        dest.writeDouble(minHeightM);
        dest.writeDouble(maxHeightM);
    }
}
