package josemanuel.marin.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.recyclerview.ListOfferItem;
import josemanuel.marin.finalproject.recyclerview.ShowOffer;

public class WarnMarketActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    Button addButton;
    SearchView searchView;
    List<ListOfferItem> offerItems;
    PrintWriter out = null;
    BufferedReader in = null;
    getOffers getOffers;
    ShowOffer adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn_market);

        addButton = findViewById(R.id.addButton);
        searchView = findViewById(R.id.searchView);

        init();

        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(WarnMarketActivity.this, AddOffer.class);
            startActivity(intent);
        });
    }

    public void init() {
        offerItems = new ArrayList<>();

        getOffers = new getOffers();

        try {
            String fromServer = getOffers.execute().get();
            //Separo la información usando ':'. Así se quedaría dividido en todas las ofertas existentes.
            String[] allOffers = fromServer.split(":");

            boolean comprobarMetros = false;

            //Ahora divido cada una de las ofertas. Las ofertas empiezan en la posición 2.
            for (int i = 2; i < allOffers.length; i++) {
                String[] offer = allOffers[i].split("_");
                List<String> tags = new ArrayList<>();

                //Ahora calculo la distancia de la persona al supermercado
                double distancia = distanciaCoord(MainActivity.latitud, MainActivity.longitud, Double.parseDouble(offer[4]), Double.parseDouble(offer[5]), 'K');
                distancia = Math.round(distancia);

                if (distancia < 1) {
                    distancia = distanciaCoord(MainActivity.latitud, MainActivity.longitud, Double.parseDouble(offer[4]), Double.parseDouble(offer[5]), 'M');
                    distancia = Math.round(distancia);
                    comprobarMetros = true;
                }

                int distanciaParsed = (int) distancia;

                //Obtengo las etiquetas, que están desde la posición 6 hasta el final
                for (int j = 6; j < offer.length; j++) {
                    tags.add(offer[j].toLowerCase());
                }

                //Añado la oferta a la lista de ofertas para que se visualicen en el recyclerview
                if (comprobarMetros) {
                    offerItems.add(new ListOfferItem(offer[3], tags, "A menos de " + distanciaParsed + " m.", offer[1] + "€", offer[2], offer[0]));
                } else {
                    offerItems.add(new ListOfferItem(offer[3], tags, "A menos de " + distanciaParsed + " km.", offer[1] + "€", offer[2], offer[0]));
                }
               //tags = "";
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //Arrays.sort(offerItems, (a, b) -> a..compareTo(b.name));

        adapter = new ShowOffer(offerItems, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filtrarDatos(newText);
        return false;
    }

    class getOffers extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            out = MainActivity.out;
            in = MainActivity.in;

            out.println("CL:" + "showOffer:"+MainActivity.latitud+":"+MainActivity.longitud);
            String offers = "";
            try {
                offers = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return offers;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    //Calcular distnacia entre dos puntos geográficos
    public static double distanciaCoord(double lat1, double lng1, double lat2, double lng2, Character unidad) {
        double radioTierra;
        if (unidad == 'M') {
            radioTierra = 6371000;//en metros
        } else {
            radioTierra = 6371;//en kilómetros
        }
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));

        return radioTierra * va2;
    }
}