package ca.stclaircollege.fitgrind.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ca.stclaircollege.fitgrind.api.Food;
import ca.stclaircollege.fitgrind.api.FoodAPI;
import ca.stclaircollege.fitgrind.api.Nutrient;

/**
 * DatabaseClassHandler class.
 * This handles the process of CRUD operations in SQLite, as well as table and data creation.
 * This will help us in tracking their calorie log, food log and other things that we will need.
 * @author Johnny Nguyen
 * @version 1.0
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    // database version. Any DB Schema updates will require an increment version
    private static final int DB_VERSION = 13;

    // Follow suit with our db fitgrind name
    private static final String DB_NAME = "fitgrind.db";

    // table name
    private static final String WEIGHTLOG_TABLE_NAME = "weight_log";
    private static final String FOOD_TABLE_NAME = "food";
    private static final String FOODLOG_TABLE_NAME = "food_log";
    private static final String WORKOUTDAY_TABLE_NAME = "workout_day";
    private static final String WORKOUTROUTINE_TABLE_NAME = "workout_routine";
    private static final String EXERCISE_TABLE_NAME = "exercise";
    private static final String CARDIOLOG_TABLE_NAME = "cardio_log";
    private static final String STRENGTHLOG_TABLE_NAME = "strength_log";
    private static final String WORKOUT_TABLE_NAME = "workout";
    private static final String PROGRESS_TABLE_NAME = "progress";

    // put it in a hashmap key
    private static final HashMap<String, String> NUTRIENT_KEYS = new HashMap<String, String>();
    private static final HashMap<String, String> CALORIE_KEY = new HashMap<String, String>();

    // initialize for our static provider
    // TODO: Fix this horror at some point
    static {
        NUTRIENT_KEYS.put("Fiber, total dietary", "fiber");
        NUTRIENT_KEYS.put("Vitamin A, RAE", "vitamin_a");
        NUTRIENT_KEYS.put("Calcium, Ca", "calcium");
        NUTRIENT_KEYS.put("Sugars, total", "sugar");
        NUTRIENT_KEYS.put("Protein", "protein");
        NUTRIENT_KEYS.put("Vitamin C, total ascorbic acid", "vitamin_c");
        NUTRIENT_KEYS.put("Total lipid (fat)", "total_fat");
        NUTRIENT_KEYS.put("Iron, Fe", "iron");
        NUTRIENT_KEYS.put("Carbohydrate, by difference", "carbohydrate");
        NUTRIENT_KEYS.put("Cholesterol", "cholesterol");
        NUTRIENT_KEYS.put("Potassium, K", "potassium");
        NUTRIENT_KEYS.put("Calories", "calories");
        NUTRIENT_KEYS.put("Sodium, Na", "sodium");
        NUTRIENT_KEYS.put("Fatty acids, total trans", "trans_fat");
        NUTRIENT_KEYS.put("Fatty acids, total saturated", "saturated_fat");
        // now do it for CALORIE_KEY map
        CALORIE_KEY.put("calories", "Calories");
        CALORIE_KEY.put("sugar", "Sugars, total");
        CALORIE_KEY.put("total_fat", "Total lipid (fat)");
        CALORIE_KEY.put("carbohydrate", "Carbohydrate, by difference");
        CALORIE_KEY.put("trans_fat", "Fatty acids, total trans");
        CALORIE_KEY.put("cholesterol", "Cholesterol");
        CALORIE_KEY.put("sodium", "Sodium, Na");
        CALORIE_KEY.put("fiber", "Fiber, total dietary");
        CALORIE_KEY.put("protein", "Protein");
        CALORIE_KEY.put("vitamin_a", "Vitamin A, RAE");
        CALORIE_KEY.put("vitamin_c", "Vitamin C, total ascorbic acid");
        CALORIE_KEY.put("calcium", "Calcium, Ca");
        CALORIE_KEY.put("iron", "Iron, Fe");
        CALORIE_KEY.put("potassium", "Potassium, K");
        CALORIE_KEY.put("saturated_fat", "Fatty acids, total saturated");
    }

    // create our table names
    private static final String CREATE_WEIGHTLOG_TABLE =
            "CREATE TABLE weight_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "weight FLOAT, " +
                "date DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')));";

    private static final String CREATE_FOOD_TABLE =
            "CREATE TABLE food (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT, " +
                "serving TEXT, " +
                "calories FLOAT, " +
                "sugar FLOAT, " +
                "total_fat FLOAT, " +
                "carbohydrate FLOAT, " +
                "saturated_fat FLOAT, " +
                "trans_fat FLOAT, " +
                "cholesterol FLOAT, " +
                "sodium FLOAT, " +
                "fiber FLOAT, " +
                "protein FLOAT, " +
                "vitamin_a FLOAT, " +
                "vitamin_c FLOAT, " +
                "calcium FLOAT, " +
                "iron FLOAT, " +
                "potassium FLOAT);";

    private static final String CREATE_FOODLOG_TABLE =
            "CREATE TABLE food_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "food_id INTEGER REFERENCES food(id) ON DELETE CASCADE, " +
                "date DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')));";

    private static final String CREATE_PROGRESS_TABLE =
            "CREATE TABLE progress (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "weight_id INTEGER REFERENCES weight_log (id) ON DELETE CASCADE, " +
                "resource TEXT);";

    private static final String CREATE_WORKOUTDAY_TABLE =
            "CREATE TABLE workout_day (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "day VARCHAR(9));";

    private static final String CREATE_WORKOUTROUTINE_TABLE =
            "CREATE TABLE workout_routine (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "description TEXT NOT NULL);";

    private static final String CREATE_EXERCISE_TABLE =
            "CREATE TABLE exercise (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name VARCHAR(100) NOT NULL);";

    private static final String CREATE_CARDIOLOG_TABLE =
            "CREATE TABLE cardio_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "exercise_id INTEGER REFERENCES exercise(id) ON DELETE CASCADE, " +
                "time FLOAT);";

    private static final String CREATE_STRENGTHLOG_TABLE =
            "CREATE TABLE strength_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "exercise_id INTEGER REFERENCES exercise(id) ON DELETE CASCADE, " +
                "sets INTEGER, " +
                "rep INTEGER, " +
                "weight FLOAT);";

    private static final String CREATE_WORKOUT_TABLE =
            "CREATE TABLE workout (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "routine_id INTEGER REFERENCES workout_routine(id) ON DELETE CASCADE, " +
                "exercise_id INTEGER REFERENCES exercise(id) ON DELETE CASCADE, " +
                "day_id INTEGER REFERENCES workout_day(id));";

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEIGHTLOG_TABLE);
        db.execSQL(CREATE_FOOD_TABLE);
        db.execSQL(CREATE_FOODLOG_TABLE);
        db.execSQL(CREATE_PROGRESS_TABLE);
        db.execSQL(CREATE_WORKOUTDAY_TABLE);
        db.execSQL(CREATE_WORKOUTROUTINE_TABLE);
        db.execSQL(CREATE_EXERCISE_TABLE);
        db.execSQL(CREATE_CARDIOLOG_TABLE);
        db.execSQL(CREATE_STRENGTHLOG_TABLE);
        db.execSQL(CREATE_WORKOUT_TABLE);
        // next we will want to pre-populate the data. This way we know for sure it's there
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Sunday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Monday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Tuesday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Wednesday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Thursday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Friday');");
        db.execSQL("INSERT INTO workout_day(id, day) VALUES (null, 'Saturday');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop the table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + WEIGHTLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FOOD_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FOODLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PROGRESS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WORKOUTDAY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WORKOUTROUTINE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EXERCISE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CARDIOLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STRENGTHLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WORKOUT_TABLE_NAME);
        // relaunch onCreate
        onCreate(db);
    }

    // this enables me to use DELETE CASCADE which helps the headache of deleting nay child rows.
    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

    // now we wanna create our crud operations in here. We will need a ton

    /**
     * Inserts routine into sqlite db. Parameters include the routine object.
     * @param program
     */
    public long insertProgram(Program program) {
        // Create the writeable DB
        SQLiteDatabase db = getWritableDatabase();
        // Use contentvalues
        ContentValues values = new ContentValues();
        // put name and desc
        values.put("name", program.getName());
        values.put("description", program.getDescription());
        return db.insert(WORKOUTROUTINE_TABLE_NAME, null, values);
    }

    public long insertWeight(Weight weight) {
        // create a writeable db
        SQLiteDatabase db = getWritableDatabase();
        // create content values
        ContentValues values = new ContentValues();
        // put the properties
        values.put("weight", weight.getWeight());
        values.put("date", weight.getDate());
        // return
        return db.insert(WEIGHTLOG_TABLE_NAME, null, values);
    }

    /**
     * Inserts a workout, with the cardio object
     * @param cardio
     */
    public boolean insertWorkout(Cardio cardio, long routineId, long dayId) {
        // writeable db
        SQLiteDatabase db = getWritableDatabase();
        // create content values
        ContentValues values = new ContentValues();
        // input the values
        values.put("name", cardio.getName());
        // after inserting we want the id
        long id = db.insert(EXERCISE_TABLE_NAME, null, values);
        values.clear(); // clear
        values.put("exercise_id", id);
        values.put("time", cardio.getTime());
        long row = db.insert(CARDIOLOG_TABLE_NAME, null, values);
        // now we finally want to insert the final workout
        values.clear();
        values.put("routine_id", routineId);
        values.put("exercise_id", id);
        values.put("day_id", dayId);
        long secondRow = db.insert(WORKOUT_TABLE_NAME, null, values);
        return row > 0 && secondRow > 0;
    }

    /**
     * Inserts workout with strength object
     * @param strength
     */
    public boolean insertWorkout(Strength strength, long routineId, long dayId) {
        // create db
        SQLiteDatabase db = getWritableDatabase();
        // create the content values
        ContentValues values = new ContentValues();
        // input the name and insert.
        values.put("name", strength.getName());
        // insert and retrieve the id
        long id = db.insert(EXERCISE_TABLE_NAME, null, values);
        values.clear();
        // insert again
        values.put("exercise_id", id);
        values.put("sets", strength.getSet());
        values.put("rep", strength.getReptitions());
        values.put("weight", strength.getWeight());
        long row = db.insert(STRENGTHLOG_TABLE_NAME, null, values);
        // now we finally want to insert the final workout
        values.clear();
        values.put("routine_id", routineId);
        values.put("exercise_id", id);
        values.put("day_id", dayId);
        long secondRow = db.insert(WORKOUT_TABLE_NAME, null, values);
        return row > 0 && secondRow > 0;
    }

    /**
     * Inserts your 'weight-log' picture weekly.
     * @param progress
     */
    public boolean insertProgress(Progress progress, long weightId) {
        SQLiteDatabase db = getWritableDatabase();
        // Create the content values
        ContentValues values = new ContentValues();
        // input the values
        values.put("resource", progress.getResource());
        values.put("weight_id", weightId);
        // insert the db
        return db.insert(PROGRESS_TABLE_NAME, null, values) > 0;
    }

    /**
     * Inserts food into db. We need to adjust a few things for the food.
     * @param food
     * @return id value
     */
    public long insertFood(Food food) {
        SQLiteDatabase db = getWritableDatabase();
        // Create the content values inside
        ContentValues values = new ContentValues();
        values.put("name", food.getName());
        values.put("serving", food.getServingSize());
        // we now have to iterate through an array to make sure
        // we can reference the map using a dictionary for access
        for (Nutrient nutrient : food.getNutrients()) values.put(NUTRIENT_KEYS.get(nutrient.getNutrient()), nutrient.getValue());
        // now finally insert from the values
        long id = db.insert(FOOD_TABLE_NAME, null, values);
        return id;
    }

    /**
     * Inserts a custom type of food. A few things need to be adjusted.
     * @param food
     * @return
     */
    public long insertCustomFood(Food food) {
        SQLiteDatabase db = getWritableDatabase();
        // create content values
        ContentValues values = new ContentValues();
        values.put("name", food.getName());
        values.put("serving", food.getServingSize());
        for (Nutrient nutrient : food.getNutrients()) values.put(nutrient.getNutrient(), nutrient.getValue());
        long id = db.insert(FOOD_TABLE_NAME, null, values);
        return id;
    }

    /**
     * Inserts from the food log
     * @param foodId
     */
    public long insertFoodLog(long foodId) {
        SQLiteDatabase db = getWritableDatabase();
        // create the content values
        ContentValues values = new ContentValues();
        values.put("food_id", foodId);
        // now insert
        long id = db.insert(FOODLOG_TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /*
     * UPDATE METHODS
     */

    /**
     * Updates specific row
     * @param program
     */
    public boolean updateRoutine(Program program) {
        // create db
        SQLiteDatabase db = getWritableDatabase();
        // setup content values
        ContentValues values = new ContentValues();
        // update the values
        values.put("name", program.getName());
        values.put("description", program.getDescription());
        // update the db

        return db.update(WORKOUTROUTINE_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(program.getId())}) > 0;
    }

    /**
     * Updates with the cardio table
     * @param cardio
     */
    public boolean updateWorkout(Cardio cardio) {
        // writeable db
        SQLiteDatabase db = getWritableDatabase();
        // create content values
        ContentValues values = new ContentValues();
        // input the values
        values.put("name", cardio.getName());
        // update db
        int row = db.update(EXERCISE_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(cardio.getId())});
        // now update on this workout
        values.clear(); // clear
        values.put("time", cardio.getTime());
        int secondRow = db.update(CARDIOLOG_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(cardio.getCardioId())});
        return row > 0 && secondRow > 0;
    }

    /**
     * Updates with the strength table
     * @param strength
     */
    public boolean updateWorkout(Strength strength) {
        // create db
        SQLiteDatabase db = getWritableDatabase();
        // create the content values
        ContentValues values = new ContentValues();
        // input the name and insert.
        values.put("name", strength.getName());
        // insert and retrieve the id
        int row = db.update(EXERCISE_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(strength.getId())});
        // update on this field
        values.clear();
        values.put("sets", strength.getSet());
        values.put("rep", strength.getReptitions());
        values.put("weight", strength.getWeight());
        int secondRow = db.update(STRENGTHLOG_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(strength.getStrengthId())});
        return row > 0 && secondRow > 0;
    }

    public boolean updateFood(Food food) {
        // Create db
        SQLiteDatabase db = getWritableDatabase();
        // Create the content values
        ContentValues values = new ContentValues();
        // we'll just go through every list, but we'll need to get the hash map entry set again
        for (Nutrient nutrient : food.getNutrients()) values.put(NUTRIENT_KEYS.get(nutrient.getNutrient()), nutrient.getValue());
        // get the rows affected
        return db.update(FOOD_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(food.getId())}) > 0;
    }

    public boolean updateWeight(Weight weight) {
        // create update db
        SQLiteDatabase db = getWritableDatabase();
        // create content values
        ContentValues values = new ContentValues();
        // put values in
        values.put("weight", weight.getWeight());
        values.put("date", weight.getDate());
        // return rows affected
        return db.update(WEIGHTLOG_TABLE_NAME, values, "id = ?", new String[]{String.valueOf(weight.getId())}) > 0;
    }

    /**
     * delete routine
     * @param id
     */
    public boolean deleteRoutine(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(WORKOUTROUTINE_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0 ;
    }

    /**
     * Deletes the cardio workout from 3 tables
     * @param id
     */
    public boolean deleteCardioWorkout(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(CARDIOLOG_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    /**
     * Deletes the strength/weights workout from 3 tables.
     * @param id
     */
    public boolean deleteStrengthWorkout(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(STRENGTHLOG_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    /**
     * Deletes the picture
     * @param id
     */
    public boolean deleteProgress(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(PROGRESS_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteProgressByWeight(long weightId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(PROGRESS_TABLE_NAME, "weight_id = ?", new String[]{String.valueOf(weightId)}) > 0;
    }

    /**
     * Deletes the food based on id
     * @param id
     * @return true if successful
     */
    public boolean deleteFood(long id) {
        SQLiteDatabase db = getWritableDatabase();
        // deletes both of them and checks if both are deleted
        return db.delete(FOOD_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    /**
     * Deletes the weight log based on id
     * @param id
     * @return true or false, true if query is successful
     */
    public boolean deleteWeight(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(WEIGHTLOG_TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    /*
     * READ METHODS / SELECT METHODS
     */

    public ArrayList<Weight> selectAllWeightLog() {
        // get a readable db
        SQLiteDatabase db = getReadableDatabase();
        // create blank arraylist
        ArrayList<Weight> results = new ArrayList<Weight>();;
        // create sql
        Cursor cursor = db.rawQuery("SELECT * FROM " + WEIGHTLOG_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            // iterate through
            do {
                results.add(new Weight(cursor.getLong(0), cursor.getDouble(1), cursor.getString(2)));
            } while(cursor.moveToNext());
        }
        return results;
    }

    public ArrayList<Progress> selectAllProgress() {
        // use a readable db
        SQLiteDatabase db = getReadableDatabase();
        // create a blank arraylist
        ArrayList<Progress> results = new ArrayList<Progress>();
        // create sql
        Cursor cursor = db.rawQuery("SELECT id, resource FROM " + PROGRESS_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            // iterate through the processsed sql
            do {
                // 0 = id, 1 = resourcce id
                results.add(new Progress(cursor.getLong(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        return results;
    }

    /**
     * Gets all the routine available that the person made.
     * @return an ArrayList of Program
     */
    public ArrayList<Program> selectAllRoutine() {
        // get readable db
        SQLiteDatabase db = getReadableDatabase();
        // create results
        ArrayList<Program> results = new ArrayList<Program>();
        // CREATE A QUERY
        Cursor cursor = db.rawQuery("SELECT * FROM " + WORKOUTROUTINE_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            // go through a do-while
            do {
                // get the required string
                results.add(new Program(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        db.close();
        // return specified results
        return results;
    }

    /**
     * Method to retrieve all of the workout.
     * @return An abstract list of all the workouts. You will need to use polymorphism to find it out
     */
    public ArrayList<WorkoutType> selectAllWorkoutAt(long dayId, long routineId) {
        // to find out which one to return, we will use an abstract class in which that it relates to both
        // get db
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<WorkoutType> workoutList = new ArrayList<WorkoutType>();
        // check for cardio log
        Cursor cursor = db.rawQuery("SELECT * FROM exercise INNER JOIN cardio_log ON exercise.id = cardio_log.exercise_id INNER JOIN workout ON exercise.id = workout.exercise_id WHERE day_id = ? AND routine_id = ?", new String[]{String.valueOf(dayId), String.valueOf(routineId)});
        if (cursor.moveToFirst()) {
            do {
                // now we can get the info for cardio log
                workoutList.add(new Cardio(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), Double.parseDouble(cursor.getString(cursor.getColumnIndex("cardio_log.time")))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // now we check for other log
        Cursor cursor2 = db.rawQuery("SELECT * FROM exercise INNER JOIN strength_log ON exercise.id = strength_log.exercise_id INNER JOIN workout ON exercise.id = workout.exercise_id WHERE day_id = ? AND routine_id = ?", new String[]{String.valueOf(dayId), String.valueOf(routineId)});
        if (cursor2.moveToFirst()) {
            do {
                // add for strength log
                workoutList.add(new Strength(cursor2.getInt(0), cursor2.getString(1), cursor2.getInt(2), cursor2.getInt(3), cursor2.getInt(4), cursor2.getDouble(cursor2.getColumnIndex("strength_log.weight"))));
            } while (cursor2.moveToNext());
        }
        db.close();
        return workoutList;
    }

    /**
     * Selects food based on id
     * @param id
     * @return
     */
    public Food selectFood(long id) {
        SQLiteDatabase db = getReadableDatabase();
        // create Food Object
        Food food = null;
        // and now create sql
        Cursor cursor = db.rawQuery("SELECT * FROM food WHERE id = ?", new String[]{String.valueOf(id)});
        // check if successful
        if (cursor.moveToFirst()) {
            // long id, String name, String servingSize, ArrayList<Nutrient> nutrients
            food = new Food(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
            // iterate the hashmap (not good) but we'll need it
            for (String key : NUTRIENT_KEYS.values()) food.addNutrient(new Nutrient(CALORIE_KEY.get(key), cursor.getDouble(cursor.getColumnIndex(key))));
        }
        return food;
    }

    /**
     * Selects progress at id
     * @param id
     * @return an object is found, null if not
     */
    public Progress selectProgress(long id) {
        Progress progress = null;
        // create db
        SQLiteDatabase db = getReadableDatabase();
        // create a query cursor to check
        Cursor cursor = db.rawQuery("SELECT * FROM progress WHERE weight_id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            // move to last because we know what it is now
            cursor.moveToLast();
            progress = new Progress(cursor.getLong(0), cursor.getString(2));
        }
        return progress;
    }

    /**
     * Retrieves a log of food, from the past x days.
     * @return FoodLog object. We know the exact amount the size of the outer, which is 7 for 7 days.
     */
    public FoodLog selectCalorieLogAt(int day) {
        FoodLog foodLog = null;
        ArrayList<Food> foodList = new ArrayList<Food>();
        // get db
        SQLiteDatabase db = getReadableDatabase();
        // create the dates
        String now = getCurrDateMinus(day);
        String sql = "SELECT food_log.date, food.* FROM food_log INNER JOIN food ON food_log.food_id = food.id " +
                "WHERE food_log.date BETWEEN ? AND ?;";
        Cursor cursor = db.rawQuery(sql, new String[]{now + " 00:00:00", now + "23:59:59"});
        // check
        if (cursor.moveToFirst()) {
            do {
                Food food = new Food(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(0));
                // we'll be forced to iterate through the hash map to get the key values
                for (String key : NUTRIENT_KEYS.values()) food.addNutrient(new Nutrient(key, cursor.getDouble(cursor.getColumnIndex(key))));
                foodList.add(food);
            } while (cursor.moveToNext());
            // return food log
            foodLog = new FoodLog(now, foodList);
        }
        // return null if nothing
        return foodLog;
    }

    public double[] selectNutrientsAt(int day) {
        double[] nutrients = null;
        // get db
        SQLiteDatabase db = getReadableDatabase();
        // create the dates
        String now = getCurrDateMinus(day);
        String sql = "SELECT SUM(food.calories), SUM(food.total_fat), SUM(food.carbohydrate), SUM(food.protein) FROM food_log INNER JOIN food ON food_log.food_id = food.id " +
                "WHERE food_log.date BETWEEN ? AND ?;";
        Cursor cursor = db.rawQuery(sql, new String[]{now + " 00:00:00", now + "23:59:59"});
        // check
        if (cursor.moveToFirst()) {
            nutrients = new double[4];
            cursor.moveToLast();
            // and now get the value
            nutrients[0] = cursor.getDouble(0);
            nutrients[1] = cursor.getDouble(1);
            nutrients[2] = cursor.getDouble(2);
            nutrients[3] = cursor.getDouble(3);
        }
        // return -1
        return nutrients;
    }

    /**
     * Selects calories
     * @param day
     * @return calories value
     */
    public double selectCaloriesAt(int day) {
        double calories = -1;
        // get db
        SQLiteDatabase db = getReadableDatabase();
        // create the dates
        String now = getCurrDateMinus(day);
        String sql = "SELECT SUM(food.calories) FROM food_log INNER JOIN food ON food_log.food_id = food.id " +
                "WHERE food_log.date BETWEEN ? AND ?;";
        Cursor cursor = db.rawQuery(sql, new String[]{now + " 00:00:00", now + "23:59:59"});
        // check
        if (cursor.moveToFirst()) {
            cursor.moveToLast();
            // and now get the value
            calories = cursor.getDouble(0);
        }
        // return -1
        return calories;
    }

    /**
     * Selects the 10 recent food logs
     * @return An arrayList of food
     */
    public ArrayList<Food> selectRecentFoodLog() {
        ArrayList<Food> foodList = null;
        // start db
        SQLiteDatabase db = getReadableDatabase();
        // create sql statement
        String sql = "SELECT food_log.date, food.id, food.name, food.serving, food.calories FROM food_log INNER JOIN food ON food_log.food_id = food.id " +
                "ORDER BY food_log.date LIMIT 10;";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            foodList = new ArrayList<Food>();
            do {
                Food food = new Food(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(0));
                // we'll be forced to iterate through the hash map to get the key values
                food.addNutrient(new Nutrient("calories", cursor.getDouble(cursor.getColumnIndex("calories"))));
                foodList.add(food);

            } while (cursor.moveToNext());
        }
        return foodList;
    }

    private String getCurrDateMinus(int day) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -day);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    // get last record for each category

    /**
     * Gets the last recorded weight log in the weight log table
     * @return String date value in ISO format
     */
    public String lastRecordedWeightLog() {
        String result = null;
        // set up db
        SQLiteDatabase db = getReadableDatabase();
        // set up query
        Cursor cursor = db.rawQuery("SELECT date FROM weight_LOG ORDER BY date DESC LIMIT 1;", null);
        if (cursor.moveToLast()) result = cursor.getString(0);
        // return result
        return result;
    }

    /**
     * Gets the last recorded calorie log from the table.
     * @return String date value
     */
    public String lastRecordedCalorieLog() {
        String result = null;
        // set up db
        SQLiteDatabase db = getReadableDatabase();
        // set up query
        Cursor cursor = db.rawQuery("SELECT strftime('%Y-%m-%d', date) FROM food_log ORDER by date DESC LIMIT 1;", null);
        if (cursor.moveToLast()) result = cursor.getString(0);
        return result;
    }

    /**
     * Checks if the progress table is empty
     * @return boolean value
     */
    public boolean isProgressEmpty() {
        // read the db
        SQLiteDatabase db = getReadableDatabase();
        // create a sql for count
        Cursor cursor = db.rawQuery("SELECT count(*) FROM progress", null);
        // check
        if (cursor.moveToFirst()) {
            // if there's a ton then we know there's something here
            if (cursor.getInt(0) == 0) {
                return true;
            }
            return false;
        }
        return false;
    }
}
