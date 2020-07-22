package ca.stclaircollege.fitgrind.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import ca.stclaircollege.fitgrind.api.Food;

/**
 * Created by johnnynguyen on 2017-04-05.
 */

public class FoodLog implements Parcelable {
    private String date;
    private ArrayList<Food> foodList;

    public FoodLog(String date, ArrayList<Food> foodList) {
        this.date = date;
        this.foodList = foodList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Food> getFoodList() {
        return foodList;
    }

    public void setFoodList(ArrayList<Food> foodList) {
        this.foodList = foodList;
    }

    protected FoodLog(Parcel in) {
        date = in.readString();
        if (in.readByte() == 0x01) {
            foodList = new ArrayList<Food>();
            in.readList(foodList, Food.class.getClassLoader());
        } else {
            foodList = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        if (foodList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(foodList);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FoodLog> CREATOR = new Parcelable.Creator<FoodLog>() {
        @Override
        public FoodLog createFromParcel(Parcel in) {
            return new FoodLog(in);
        }

        @Override
        public FoodLog[] newArray(int size) {
            return new FoodLog[size];
        }
    };

}
