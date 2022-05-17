package josemanuel.marin.finalproject.recyclerview;

import java.util.List;

public class ListOfferItem implements Comparable<ListOfferItem> {
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

    public void setDistance(String distance) {
        this.distance = distance;
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

    @Override
    public int compareTo(ListOfferItem o) {
        return distance.compareTo(o.distance);
    }
}
