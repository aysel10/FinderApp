package android.tapmisam.az.testregister;

import java.util.ArrayList;

/**
 * Created by ayselkas on 12/5/17.
 */

public class Item {
    private String description;
    private String title;
    private String imagePath;
    private int id;

    public Item(int id, String description, String title, String imagePath) {
        this.id=id;
        this.description = description;
        this.title = title;
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return description+"  "+title+"  "+imagePath;
    }
}
