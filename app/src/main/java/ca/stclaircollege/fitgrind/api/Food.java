package ca.stclaircollege.fitgrind.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by johnnynguyen on 2017-03-30.
 */
public class Food implements Parcelable {
    public static final String NAME_KEY = "name";
    public static final String WEIGHT_KEY = "weight";
    public static final String MEASURE_KEY = "measure";

    private long id;
    private String logDate;
    private String name;
    private String servingSize;
    // always set it by default to max nutrients. This is great
    private ArrayList<Nutrient> nutrients;

    public Food(String name, String servingSize) {
        this.name = (name.indexOf(", UPC") != -1) ? name.substring(0, name.indexOf(", UPC")) : name;
        this.servingSize = servingSize;
        // instantiate new array list
        nutrients = new ArrayList<Nutrient>();
    }

    public Food(long id, String name, String servingSize) {
        this.id = id;
        this.name = name;
        this.servingSize = servingSize;
        this.nutrients = new ArrayList<Nutrient>();
    }

    public Food(long id, String name, String servingSize, String logDate) {
        this.id = id;
        this.name = name;
        this.servingSize = servingSize;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(logDate);
            this.logDate = new SimpleDateFormat("yyyy-MM-dd K:mm a").format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            this.logDate = logDate;
        }
        this.nutrients = new ArrayList<Nutrient>();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addNutrient(Nutrient nutrient) {
        nutrients.add(nutrient);
    }

    public String getName() {
        return name;
    }

    public String getServingSize() {
        return servingSize;
    }

    public long getId() {
        return id;
    }

    public String getLogDate() {
        return logDate;
    }

    public ArrayList<Nutrient> getNutrients() {
        return nutrients;
    }

    public Nutrient getNutrient(String name) {
        for (Nutrient nutrient : nutrients) {
            if (nutrient.getNutrient().equalsIgnoreCase("calories")) return nutrient;
        }
        return null;
    }

    protected Food(Parcel in) {
        id = in.readLong();
        logDate = in.readString();
        name = in.readString();
        servingSize = in.readString();
        if (in.readByte() == 0x01) {
            nutrients = new ArrayList<Nutrient>();
            in.readList(nutrients, Nutrient.class.getClassLoader());
        } else {
            nutrients = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(logDate);
        dest.writeString(name);
        dest.writeString(servingSize);
        if (nutrients == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(nutrients);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Food> CREATOR = new Parcelable.Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };
}
