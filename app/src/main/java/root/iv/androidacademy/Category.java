package root.iv.androidacademy;


import java.io.Serializable;

public class Category implements Serializable {
    private final int id;
    private final String name;
    private final int colorRes;

    public Category(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.colorRes = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return colorRes;
    }
}