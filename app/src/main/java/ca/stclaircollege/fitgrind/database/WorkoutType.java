package ca.stclaircollege.fitgrind.database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johnnynguyen on 2017-04-02.
 */

public abstract class WorkoutType implements Parcelable {
    protected long id;
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    protected WorkoutType(long id, String name) {
        this.id = id;
        this.name = name;
    }

    protected WorkoutType(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
    }
}
