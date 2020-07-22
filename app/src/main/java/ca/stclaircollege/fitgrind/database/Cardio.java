package ca.stclaircollege.fitgrind.database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johnnynguyen on 2017-04-02.
 */

public class Cardio extends WorkoutType {
    private long cardioId;
    private Double time;

    public Cardio(long cardioId, String name, Double time) {
        super(-1, name);
        this.name = name;
        this.cardioId = cardioId;
        this.time = time;
    }

    public Cardio(long id, String name, long cardioId, Double time) {
        super(id, name);
        this.id = id;
        this.name = name;
        this.cardioId = cardioId;
        this.time = time;
    }

    public Cardio(String name, Double time) {
        super(-1, name);
        this.name = name;
        this.time = time;
    }


    public long getCardioId() {
        return cardioId;
    }

    public void setCardioId(long cardioId) {
        this.cardioId = cardioId;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    private Cardio(Parcel in) {
        super(in);
        cardioId = in.readLong();
        time = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(cardioId);
        dest.writeDouble(time);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Cardio> CREATOR = new Parcelable.Creator<Cardio>() {
        @Override
        public Cardio createFromParcel(Parcel in) {
            return new Cardio(in);
        }

        @Override
        public Cardio[] newArray(int size) {
            return new Cardio[size];
        }
    };
}
