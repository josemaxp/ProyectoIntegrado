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
    String price;
    String priceUnity;
    String username;
    String image;
    String poblacion;
    String provincia;
    String comunidadAutonoma;

    public OfferItem(String market, List<String> tags, String price, String priceUnity, String username,String image, boolean approvedOffer, int id, String poblacion, String provincia, String comunidadAutonoma) {
        this.market = market;
        this.tags = tags;
        this.price = price;
        this.priceUnity = priceUnity;
        this.image = image;
        this.username = username;
        this.poblacion = poblacion;
        this.provincia = provincia;
        this.comunidadAutonoma = comunidadAutonoma;
    }

    public String getMarket() {
        return market;
    }

    public List<String> getTags() {
        return tags;
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

    public String getPoblacion() {
        return poblacion;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getComunidadAutonoma() {
        return comunidadAutonoma;
    }

    public String getImage() {
        return image;
    }
}
