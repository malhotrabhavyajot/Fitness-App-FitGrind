package ca.stclaircollege.fitgrind.database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Allan on 4/1/2017.
 */

public class Program implements Parcelable {
    private long id;
    private String name;
    private String description;

    public Program(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Program(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Program(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Program> CREATOR = new Parcelable.Creator<Program>() {
        @Override
        public Program createFromParcel(Parcel in) {
            return new Program(in);
        }

        @Override
        public Program[] newArray(int size) {
            return new Program[size];
        }
    };
}
