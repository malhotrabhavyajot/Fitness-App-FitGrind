package ca.stclaircollege.fitgrind.database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johnnynguyen on 2017-04-02.
 */

public class Strength extends WorkoutType {
    private long strengthId;
    private int set;
    private int reptitions;
    private double weight;

    public Strength(long strengthId, String name, int set, int reptitions, double weight) {
        super(-1, name);
        this.strengthId = strengthId;
        this.name = name;
        this.set = set;
        this.reptitions = reptitions;
        this.weight = weight;
    }

    public Strength(long id, String name, long strengthId, int set, int reptitions, double weight) {
        super(id, name);
        this.id = id;
        this.strengthId = strengthId;
        this.name = name;
        this.set = set;
        this.reptitions = reptitions;
        this.weight = weight;
    }

    public Strength(String name, int set, int reptitions, double weight) {
        super(-1, name);
        this.name = name;
        this.set = set;
        this.reptitions = reptitions;
        this.weight = weight;
    }

    public long getStrengthId() {
        return strengthId;
    }

    public void setStrengthId(long strengthId) {
        this.strengthId = strengthId;
    }

    public int getSet() {
        return set;
    }

    public void setSet(int set) {
        this.set = set;
    }

    public int getReptitions() {
        return reptitions;
    }

    public void setReptitions(int reptitions) {
        this.reptitions = reptitions;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    private Strength(Parcel in) {
        super(in);
        strengthId = in.readLong();
        set = in.readInt();
        reptitions = in.readInt();
        weight = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(strengthId);
        dest.writeInt(set);
        dest.writeInt(reptitions);
        dest.writeDouble(weight);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Strength> CREATOR = new Parcelable.Creator<Strength>() {
        @Override
        public Strength createFromParcel(Parcel in) {
            return new Strength(in);
        }

        @Override
        public Strength[] newArray(int size) {
            return new Strength[size];
        }
    };
}
