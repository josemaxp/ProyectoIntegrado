package josemanuel.marin.finalproject.model;

import java.util.List;

public class ListOfferItem implements Comparable<ListOfferItem> {
    int id;
    String market;
    List<String> tags;
    String distance;
    String price;
    String priceUnity;
    String username;
    String image;
    Double latitud;
    Double longitud;
    boolean approvedOffer;

    public ListOfferItem(String market, List<String> tags, String distance, String price, String priceUnity, String username, String image, boolean approvedOffer, int id, double latitud, double longitud) {
        this.id = id;
        this.market = market;
        this.tags = tags;
        this.distance = distance;
        this.price = price;
        this.priceUnity = priceUnity;
        this.username = username;
        this.image = image;
        this.approvedOffer = approvedOffer;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getID() {
        return id;
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

    public String getImage() {
        return image;
    }

    public Boolean getApprovedOffer() {
        return approvedOffer;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    @Override
    public int compareTo(ListOfferItem o) {
        return distance.compareTo(o.distance);
    }
}
