/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.List;

/**
 *
 * @author josem
 */
public class OfferItem {
    String market;
    List<String> tags;
    String distance;
    String price;
    String priceUnity;
    String username;
    //String image;

    public OfferItem(String market, List<String> tags, String distance, String price, String priceUnity, String username) {
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

    public int compareTo(OfferItem o) {
        return distance.compareTo(o.distance);
    }
}
