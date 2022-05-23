package josemanuel.marin.finalproject;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import josemanuel.marin.finalproject.model.ListOfferItem;

public class MyOffers extends AppCompatActivity {
    SearchView searchView;
    List<ListOfferItem> offerItems;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_offers);

        searchView = findViewById(R.id.searchViewMyOffers);


    }
}
