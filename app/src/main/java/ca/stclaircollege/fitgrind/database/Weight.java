package ca.stclaircollege.fitgrind.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by johnnynguyen on 2017-04-12.
 */

public class Weight {
    private long id;
    private double weight;
    private String date;

    public Weight(long id, double weight, String date) {
        this.id = id;
        this.weight = weight;
        this.date = date;
    }

    public Weight(double weight, String date) {
        this.weight = weight;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFormattedDate() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.getDefault());

        Date date;
        String formatDate = null;

        try {
            date = inputFormat.parse(this.date);
            formatDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formatDate;
    }
}
