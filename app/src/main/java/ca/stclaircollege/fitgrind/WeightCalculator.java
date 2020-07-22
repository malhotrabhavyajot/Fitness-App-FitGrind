package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class is used to get the shared preferences manager and calculate the information within
 * Johnny Nguyen
 */

public class WeightCalculator {

    private static double CONVERT_TO_KG = 0.453592;
    private static double CONVERT_TO_CM = 2.54;
    // for now we'll force the user to use imperial settings, because most people tend to check their weight and height by
    // imperial. Later on, we will have a setting that fixes that.
    private String gender;
    private double age, height, weight, weightgoal, lifestyle;
    private double deficit;
    private double BMR;

    public WeightCalculator(Context context) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        gender = SP.getString("gender_keys", "Male");
        age = Double.parseDouble(SP.getString("age", "0"));
        height = Double.parseDouble(SP.getString("height_inches", "0")) * CONVERT_TO_CM;
        weight = Double.parseDouble(SP.getString("weight", "0")) * CONVERT_TO_KG;
        weightgoal = Double.parseDouble(SP.getString("weight_goal", "0")) * CONVERT_TO_KG;
        lifestyle = Double.parseDouble(SP.getString("lifestyle_key", "1.2"));
        deficit = Double.parseDouble(SP.getString("weight_per_week", "500"));

        // now we check and calculate BMR formula based on given results.
        // this will calculate how much to maintain weight
        if (gender.equals("Male")) {
            BMR = (10 * weight + 6.25 * height - 5 * age + 5) * lifestyle;
        } else {
            // now do it for female
            BMR = (10 * weight + 6.25 * height - 5 * age - 161) * lifestyle;
        }

        // and now based on the given results we can do this
        BMR = (weightgoal > weight) ? BMR + deficit : BMR - deficit;
    }

    public WeightCalculator(SharedPreferences SP) {
        gender = SP.getString("gender_keys", "Male");
        age = Double.parseDouble(SP.getString("age", "0"));
        height = Double.parseDouble(SP.getString("height_inches", "0")) * CONVERT_TO_CM;
        weight = Double.parseDouble(SP.getString("weight", "0")) * CONVERT_TO_KG;
        weightgoal = Double.parseDouble(SP.getString("weight_goal", "0")) * CONVERT_TO_KG;
        lifestyle = Double.parseDouble(SP.getString("lifestyle_key", "1.2"));
        deficit = Double.parseDouble(SP.getString("weight_per_week", "500"));

        /*
         For men: BMR = 10 x weight (kg) + 6.25 x height (cm) – 5 x age (years) + 5
         For women: BMR = 10 x weight (kg) + 6.25 x height (cm) – 5 x age (years) – 161
         */

        // now we check and calculate BMR formula based on given results.
        // this will calculate how much to maintain weight
        if (gender.equals("Male")) {
            BMR = (10 * weight + 6.25 * height - 5 * age + 5) * lifestyle;
        } else {
            // now do it for female
            BMR = (10 * weight + 6.25 * height - 5 * age - 161) * lifestyle;
        }

        // and now based on the given results we can do this
        BMR = (weightgoal > weight) ? BMR + deficit : BMR - deficit;
    }

    public double getBMR() {
        return Math.floor(BMR);
    }

    public String getCurrentWeight() {
        return String.format("%.0f lbs",(weight / CONVERT_TO_KG));
    }

    public String getCalorieGoal() {
        // let's round it down automatically, instead of rounding up to be safe.
        return String.format("%.0f", Math.floor(this.BMR));
    }

    // let's return this back to lbs, so we'll have to convert again
    public String getWeightGoal() {
        return String.format("%.0f lbs", (weightgoal / CONVERT_TO_KG));
    }
}
