package josemanuel.marin.finalproject.recyclerview;

import java.util.List;

public class ListOfferItem {
    String market;
    List<String> tags;
    String distance;
    String price;
    String priceUnity;
    String username;
    //String image;

    public ListOfferItem(String market, List<String> tags, String distance, String price, String priceUnity, String username) {
        this.market = market;
        this.tags = tags;
        this.distance = distance;
        this.price = price;
        this.priceUnity = priceUnity;
        this.username = username;
    }

    public String getMarket() {
        return market;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getDistance() {
        return distance;
    }

    public String getPrice() {
        return price;
    }

    public String getPriceUnity() {
        return priceUnity;
    }

    public String getUsername() {
        return username;
    }
}
