package ca.stclaircollege.fitgrind.database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Progress class dedicated for the viewpager in creating your 'weekly' or 'monthly' progress
 *
 */

public class Progress implements Parcelable {
    private long id;
    private String resource;

    public Progress(long id, String resource) {
        this.id = id;
        this.resource = resource;
    }

    public Progress(String resource) {
        this.resource = resource;
    }

    protected Progress(Parcel in) {
        id = in.readLong();
        resource = in.readString();
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(resource);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Progress> CREATOR = new Parcelable.Creator<Progress>() {
        @Override
        public Progress createFromParcel(Parcel in) {
            return new Progress(in);
        }

        @Override
        public Progress[] newArray(int size) {
            return new Progress[size];
        }
    };
}
