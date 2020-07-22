package ca.stclaircollege.fitgrind.api;

import android.content.Context;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rx2androidnetworking.Rx2ANRequest;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * FoodAPI class is where we retrieve all our food, nutritional values and much more.
 * This is primarily where we get our requests
 * NOTE: We are now using the RetroFit Library, provided by Square. This to ensure that our APP works on Android 2.3+
 * @author Johnny Nguyen
 * @version 3.0
 */
public class FoodAPI {
    // We want to create constant URLS so we don't mess up.
    // URL Search is the URL needed for searching for a certain food item.
    // URL INFO is to get nutritional info from the search parameters.
    // We can create constants inside here to use later on
    private static final String BASE_URL = "https://api.nal.usda.gov/ndb/";
    private static final String NUTRIENT_URL = BASE_URL + "nutrients/?nutrients=208&nutrients=269&nutrients=204&nutrients=205&nutrients=606&nutrients=605" +
            "&nutrients=601&nutrients=307&nutrients=291&nutrients=203&nutrients=320&nutrients=401&nutrients=301&nutrients=303&nutrients=306";
    // A static API key works here
    private static String apiKey = "pwiN99R45M4Zs6Wj0yHYBxokOxhPYQcZ6WxbQMYt";

    private static final String LIST_KEY = "list";
    private static final String ERRORS_KEY = "errors";
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";
    private static final String ITEM_KEY = "item";
    // -------------------------------------------
    private static final String REPORT_KEY = "report";
    private static final String FOODS_KEY = "foods";
    private static final String NUTRIENT_KEY = "nutrients";

    public FoodAPI(Context context) {
        // Initialize it here
        AndroidNetworking.initialize(context);
    }

    /**
     * Searches food, and returns an observable before continuing
     * @param food
     * @return
     */
    public Observable<ArrayList<Item>> searchFood(String food) {
         return Rx2AndroidNetworking.get(BASE_URL + "search/?")
                .addQueryParameter("format", "json")
                .addQueryParameter("q", food)
                .addQueryParameter("api_key", this.apiKey)
                .build()
                .getJSONObjectObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<JSONObject, ArrayList<Item>>() {
                    @Override
                    public ArrayList<Item> apply(JSONObject json) throws Exception {
                        if (json.has(LIST_KEY)) {
                            // We now want to only get the items, so we can display it on the listview
                            JSONObject list = json.getJSONObject(LIST_KEY);
                            JSONArray jsonArray = list.getJSONArray(ITEM_KEY);
                            // create a new item list to start
                            ArrayList<Item> itemList = new ArrayList<Item>();
                            // iterate in a for loop
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                itemList.add(new Item(obj.getString(Item.GROUP_KEY), obj.getString(Item.NAME_KEY), Integer.parseInt(obj.getString(Item.NDBNO_KEY))));
                            }
                            return itemList;
                        }
                        JSONArray result = json.getJSONObject(ERRORS_KEY).getJSONArray(ERROR_KEY);
                        String message = (String) ((JSONObject) result.get(0)).get(MESSAGE_KEY);
                        throw new Exception(message);
                    }
                });

    }

    public Observable<Food> getFood(int ndbno) {
        return Rx2AndroidNetworking.get(NUTRIENT_URL)
                .addQueryParameter("format", "json")
                .addQueryParameter("ndbno", Integer.toString(ndbno))
                .addQueryParameter("api_key", this.apiKey)
                .build()
                .getJSONObjectObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<JSONObject, Food>() {
                    @Override
                    public Food apply(JSONObject json) throws Exception {
                        JSONObject report = json.getJSONObject(REPORT_KEY);
                        // retrieve the data. Because we know NDBno is unique, we expect ONLY 1.
                        JSONObject foodObj = (JSONObject) report.getJSONArray(FOODS_KEY).get(0);
                        // Create a tmp variable for better storage organize
                        String serving = foodObj.getString(Food.MEASURE_KEY) + " " + foodObj.getString(Food.WEIGHT_KEY) + "g";
                        // create the food object
                        Food currFood = new Food(foodObj.getString(Food.NAME_KEY), serving);
                        // now we want to iterate through the nutrient list json object.
                        JSONArray nutrientObj = foodObj.getJSONArray(NUTRIENT_KEY);
                        for (int i=0; i < nutrientObj.length(); i++) {
                            JSONObject obj = nutrientObj.getJSONObject(i);
                            currFood.addNutrient(new Nutrient(obj.getString(Nutrient.NUTRIENT_KEY),
                                    obj.getString(Nutrient.UNIT_KEY), obj.getString(Nutrient.VALUE_KEY)));
                        }
                        return currFood;
                    }
                });
    }

    /**
     * Food Results from specified number
     * @param ndbno
     * @param requestListener
     */
    public void foodResult(int ndbno, JSONObjectRequestListener requestListener) {
        // because there's no easy way, we'll have to add it manually like so
        AndroidNetworking.get(NUTRIENT_URL)
                .addQueryParameter("format", "json")
                .addQueryParameter("ndbno", Integer.toString(ndbno))
                .addQueryParameter("api_key", this.apiKey)
                .build()
                .getAsJSONObject(requestListener);
    }
}
