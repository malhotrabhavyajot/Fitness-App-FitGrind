package ca.stclaircollege.fitgrind.database;

/**
 * Created by Allan on 4/5/2017.
 */

public class Exercise {
    private String name;
    private String set;
    private String rep;

    public Exercise(String name, String set, String rep, String weight) {
        this.name = name;
        this.set = set;
        this.rep = rep;
    }

    public String getRep() {
        return rep;
    }

    public void setRep(String rep) {
        this.rep = rep;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String toString() {
        return getName();
    }
}
