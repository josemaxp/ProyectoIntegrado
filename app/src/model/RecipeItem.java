package model;

import java.util.List;

/**
 *
 * @author josem
 */
public class RecipeItem implements Comparable<RecipeItem>{
    int id;
    String username;
    String name;
    List<String> products;
    int people;
    String steps;
    int likes;
    String Cookware;
    String image;
    String time;

    public RecipeItem(int id,String username, String name, List<String> products, int people, String steps, int likes, String cookware, String image, String time) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.products = products;
        this.people = people;
        this.steps = steps;
        this.likes = likes;
        this.Cookware = cookware;
        this.image = image;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public List<String> getProducts() {
        return products;
    }

    public int getPeople() {
        return people;
    }

    public String getSteps() {
        return steps;
    }

    public int getLikes() {
        return likes;
    }

    public String getCookware() {
        return Cookware;
    }

    public String getImage() {
        return image;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int compareTo(RecipeItem o) {
        return String.valueOf(o.likes).compareTo(String.valueOf(likes));
    }
}
